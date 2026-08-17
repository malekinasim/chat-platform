package com.nasim.chat.client.websocket.eventListener;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Component
public class PrivateMessageSyncRegistry {

    private final ConcurrentMap<String, SessionSyncEntry> sessionsById =
            new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CompletableFuture<Void>> activeSyncsByUser =
            new ConcurrentHashMap<>();

    public boolean registerSession(String userId, String sessionId) {
        SessionSyncEntry entry = new SessionSyncEntry(
                Objects.requireNonNull(userId, "userId must not be null"),
                PrivateMessageSyncStatus.NOT_STARTED
        );
        return sessionsById.putIfAbsent(
                Objects.requireNonNull(sessionId, "sessionId must not be null"),
                entry
        ) == null;
    }

    public boolean tryStartSessionSync(String userId, String sessionId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(sessionId, "sessionId must not be null");

        AtomicBoolean transitioned = new AtomicBoolean();
        sessionsById.computeIfPresent(sessionId, (ignored, current) -> {
            if (current.userId().equals(userId)
                    && current.status() == PrivateMessageSyncStatus.NOT_STARTED) {
                transitioned.set(true);
                return current.withStatus(PrivateMessageSyncStatus.IN_PROGRESS);
            }
            return current;
        });
        return transitioned.get();
    }

    public CompletableFuture<Void> runOrJoinUserSync(
            String userId,
            Supplier<CompletableFuture<Void>> syncOperation
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(syncOperation, "syncOperation must not be null");

        CompletableFuture<Void> activeFuture = activeSyncsByUser.computeIfAbsent(
                userId,
                ignored -> {
                    try {
                        return Objects.requireNonNull(
                                syncOperation.get(),
                                "syncOperation must not return null"
                        );
                    } catch (RuntimeException error) {
                        return CompletableFuture.failedFuture(error);
                    }
                }
        );
        activeFuture.whenComplete((ignored, error) ->
                activeSyncsByUser.remove(userId, activeFuture));
        return activeFuture;
    }

    public void completeSessionSync(String sessionId) {
        transitionSession(
                sessionId,
                PrivateMessageSyncStatus.IN_PROGRESS,
                PrivateMessageSyncStatus.COMPLETED
        );
    }

    public void resetSessionSync(String sessionId) {
        transitionSession(
                sessionId,
                PrivateMessageSyncStatus.IN_PROGRESS,
                PrivateMessageSyncStatus.NOT_STARTED
        );
    }

    public void removeSession(String sessionId) {
        sessionsById.remove(Objects.requireNonNull(sessionId, "sessionId must not be null"));
    }

    public Optional<PrivateMessageSyncStatus> status(String sessionId) {
        SessionSyncEntry entry = sessionsById.get(
                Objects.requireNonNull(sessionId, "sessionId must not be null")
        );
        return entry == null ? Optional.empty() : Optional.of(entry.status());
    }

    private void transitionSession(
            String sessionId,
            PrivateMessageSyncStatus expectedStatus,
            PrivateMessageSyncStatus newStatus
    ) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        sessionsById.computeIfPresent(sessionId, (ignored, current) ->
                current.status() == expectedStatus
                        ? current.withStatus(newStatus)
                        : current
        );
    }

    private record SessionSyncEntry(
            String userId,
            PrivateMessageSyncStatus status
    ) {
        private SessionSyncEntry withStatus(PrivateMessageSyncStatus newStatus) {
            return new SessionSyncEntry(userId, newStatus);
        }
    }
}
