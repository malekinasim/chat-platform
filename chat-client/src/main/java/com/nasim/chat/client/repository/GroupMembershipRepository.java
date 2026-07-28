package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership,Long> {

    boolean existsByUserIdAndGroup_GroupCodeAndActiveTrue(String userId, String groupCode);
}
