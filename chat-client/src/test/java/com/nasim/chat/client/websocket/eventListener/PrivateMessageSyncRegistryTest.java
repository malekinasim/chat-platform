package com.nasim.chat.client.websocket.eventListener;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrivateMessageSyncRegistryTest {

    private final PrivateMessageSyncRegistry registry = new PrivateMessageSyncRegistry();

    @Test
    void registersNewSessionAsNotStarted() {
        assertThat(registry.registerSession("user", "session")).isTrue();
        assertThat(registry.status("session")).contains(PrivateMessageSyncStatus.NOT_STARTED);
    }

    @Test
    void duplicateRegistrationDoesNotOverwriteExistingState() {
        registry.registerSession("user", "session");
        registry.tryStartSessionSync("user", "session");

        assertThat(registry.registerSession("different-user", "session")).isFalse();
        assertThat(registry.status("session")).contains(PrivateMessageSyncStatus.IN_PROGRESS);
        assertThat(registry.tryStartSessionSync("different-user", "session")).isFalse();
    }

    @Test
    void startsNotStartedSessionOnlyOnce() {
        registry.registerSession("user", "session");

        assertThat(registry.tryStartSessionSync("user", "session")).isTrue();
        assertThat(registry.status("session")).contains(PrivateMessageSyncStatus.IN_PROGRESS);
        assertThat(registry.tryStartSessionSync("user", "session")).isFalse();
    }

    @Test
    void doesNotStartUnknownSessionOrSessionOwnedByAnotherUser() {
        assertThat(registry.tryStartSessionSync("user", "unknown")).isFalse();

        registry.registerSession("owner", "session");
        assertThat(registry.tryStartSessionSync("other", "session")).isFalse();
        assertThat(registry.status("session")).contains(PrivateMessageSyncStatus.NOT_STARTED);
    }

    @Test
    void concurrentStartAttemptsPermitExactlyOneTransition() throws Exception {
        registry.registerSession("user", "session");
        int callerCount = 16;
        CountDownLatch ready = new CountDownLatch(callerCount);
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(callerCount)) {
            List<Future<Boolean>> attempts = new ArrayList<>();
            for (int i = 0; i < callerCount; i++) {
                attempts.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    return registry.tryStartSessionSync("user", "session");
                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            int successes = 0;
            for (Future<Boolean> attempt : attempts) {
                if (attempt.get(5, TimeUnit.SECONDS)) {
                    successes++;
                }
            }
            assertThat(successes).isOne();
        }
    }

    @Test
    void sessionsForSameUserStartIndependently() {
        registry.registerSession("user", "first");
        registry.registerSession("user", "second");

        assertThat(registry.tryStartSessionSync("user", "first")).isTrue();
        assertThat(registry.status("first")).contains(PrivateMessageSyncStatus.IN_PROGRESS);
        assertThat(registry.status("second")).contains(PrivateMessageSyncStatus.NOT_STARTED);
        assertThat(registry.tryStartSessionSync("user", "second")).isTrue();
    }

    @Test
    void concurrentSameUserCallsShareOneFutureAndRunSupplierOnce() throws Exception {
        int callerCount = 16;
        CountDownLatch ready = new CountDownLatch(callerCount);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger invocationCount = new AtomicInteger();
        CompletableFuture<Void> operation = new CompletableFuture<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(callerCount)) {
            List<Future<CompletableFuture<Void>>> calls = new ArrayList<>();
            for (int i = 0; i < callerCount; i++) {
                calls.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    return registry.runOrJoinUserSync("user", () -> {
                        invocationCount.incrementAndGet();
                        return operation;
                    });
                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            List<CompletableFuture<Void>> results = new ArrayList<>();
            for (Future<CompletableFuture<Void>> call : calls) {
                results.add(call.get(5, TimeUnit.SECONDS));
            }
            assertThat(invocationCount).hasValue(1);
            assertThat(results).allMatch(result -> result == results.getFirst());
            assertThat(results.getFirst()).isSameAs(operation);

            operation.complete(null);
            results.getFirst().join();
        }
    }

    @Test
    void differentUsersHaveIndependentOperations() {
        CompletableFuture<Void> firstOperation = new CompletableFuture<>();
        CompletableFuture<Void> secondOperation = new CompletableFuture<>();

        CompletableFuture<Void> first = registry.runOrJoinUserSync("first", () -> firstOperation);
        CompletableFuture<Void> second = registry.runOrJoinUserSync("second", () -> secondOperation);

        assertThat(first).isSameAs(firstOperation);
        assertThat(second).isSameAs(secondOperation);
        assertThat(first).isNotSameAs(second);
        firstOperation.complete(null);
        assertThat(first).isCompleted();
        assertThat(second).isNotDone();
        secondOperation.complete(null);
    }

    @Test
    void completionChangesOnlyInProgressSessions() {
        registry.registerSession("user", "not-started");
        registry.registerSession("user", "in-progress");
        registry.tryStartSessionSync("user", "in-progress");

        registry.completeSessionSync("unknown");
        registry.completeSessionSync("not-started");
        registry.completeSessionSync("in-progress");

        assertThat(registry.status("unknown")).isEmpty();
        assertThat(registry.status("not-started")).contains(PrivateMessageSyncStatus.NOT_STARTED);
        assertThat(registry.status("in-progress")).contains(PrivateMessageSyncStatus.COMPLETED);
        registry.completeSessionSync("in-progress");
        assertThat(registry.status("in-progress")).contains(PrivateMessageSyncStatus.COMPLETED);
    }

    @Test
    void resetChangesOnlyInProgressSessions() {
        registry.registerSession("user", "completed");
        registry.tryStartSessionSync("user", "completed");
        registry.completeSessionSync("completed");
        registry.registerSession("user", "in-progress");
        registry.tryStartSessionSync("user", "in-progress");

        registry.resetSessionSync("unknown");
        registry.resetSessionSync("completed");
        registry.resetSessionSync("in-progress");

        assertThat(registry.status("unknown")).isEmpty();
        assertThat(registry.status("completed")).contains(PrivateMessageSyncStatus.COMPLETED);
        assertThat(registry.status("in-progress")).contains(PrivateMessageSyncStatus.NOT_STARTED);
    }

    @Test
    void removingOneSessionDoesNotRemoveAnotherForSameUser() {
        registry.registerSession("user", "first");
        registry.registerSession("user", "second");

        registry.removeSession("first");

        assertThat(registry.status("first")).isEmpty();
        assertThat(registry.status("second")).contains(PrivateMessageSyncStatus.NOT_STARTED);
    }

    @Test
    void completionAndResetAfterDisconnectDoNotRecreateSession() {
        registry.registerSession("user", "complete");
        registry.tryStartSessionSync("user", "complete");
        registry.removeSession("complete");
        registry.completeSessionSync("complete");

        registry.registerSession("user", "reset");
        registry.tryStartSessionSync("user", "reset");
        registry.removeSession("reset");
        registry.resetSessionSync("reset");

        assertThat(registry.status("complete")).isEmpty();
        assertThat(registry.status("reset")).isEmpty();
    }

    @Test
    void successfulCompletionAllowsLaterOperation() {
        AtomicInteger invocationCount = new AtomicInteger();
        CompletableFuture<Void> operation = new CompletableFuture<>();
        CompletableFuture<Void> first = registry.runOrJoinUserSync("user", () -> {
            invocationCount.incrementAndGet();
            return operation;
        });

        operation.complete(null);
        first.join();
        CompletableFuture<Void> second = registry.runOrJoinUserSync("user", () -> {
            invocationCount.incrementAndGet();
            return CompletableFuture.completedFuture(null);
        });

        assertThat(second).isNotSameAs(first).isCompleted();
        assertThat(invocationCount).hasValue(2);
    }

    @Test
    void exceptionalCompletionAllowsRetry() {
        AtomicInteger invocationCount = new AtomicInteger();
        CompletableFuture<Void> operation = new CompletableFuture<>();
        CompletableFuture<Void> first = registry.runOrJoinUserSync("user", () -> {
            invocationCount.incrementAndGet();
            return operation;
        });

        operation.completeExceptionally(new IllegalStateException("replay failed"));
        assertThatThrownBy(first::join).hasCauseInstanceOf(IllegalStateException.class);
        CompletableFuture<Void> retry = registry.runOrJoinUserSync("user", () -> {
            invocationCount.incrementAndGet();
            return CompletableFuture.completedFuture(null);
        });

        assertThat(retry).isCompleted();
        assertThat(invocationCount).hasValue(2);
    }

    @Test
    void alreadyCompletedFutureIsNotCached() {
        AtomicInteger invocationCount = new AtomicInteger();

        registry.runOrJoinUserSync("user", () -> {
            invocationCount.incrementAndGet();
            return CompletableFuture.completedFuture(null);
        }).join();
        registry.runOrJoinUserSync("user", () -> {
            invocationCount.incrementAndGet();
            return CompletableFuture.completedFuture(null);
        }).join();

        assertThat(invocationCount).hasValue(2);
    }

    @Test
    void synchronouslyThrowingSupplierReturnsFailedFutureAndAllowsRetry() {
        CompletableFuture<Void> failed = registry.runOrJoinUserSync("user", () -> {
            throw new IllegalStateException("could not start");
        });

        assertThatThrownBy(failed::join).hasCauseInstanceOf(IllegalStateException.class);
        assertThat(registry.runOrJoinUserSync(
                "user",
                () -> CompletableFuture.completedFuture(null)
        )).isCompleted();
    }

    @Test
    void supplierErrorIsPropagatedAndDoesNotCreateAnActiveSync() {
        AssertionError error = new AssertionError("fatal failure");

        assertThatThrownBy(() -> registry.runOrJoinUserSync("user", () -> {
            throw error;
        })).isSameAs(error);
        assertThat(registry.runOrJoinUserSync(
                "user",
                () -> CompletableFuture.completedFuture(null)
        )).isCompleted();
    }

    @Test
    void nullOperationFutureReturnsFailedFutureAndAllowsRetry() {
        CompletableFuture<Void> failed = registry.runOrJoinUserSync("user", () -> null);

        assertThatThrownBy(failed::join).hasCauseInstanceOf(NullPointerException.class);
        assertThat(registry.runOrJoinUserSync(
                "user",
                () -> CompletableFuture.completedFuture(null)
        )).isCompleted();
    }
}
