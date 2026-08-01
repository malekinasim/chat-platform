package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership,Long> {

    boolean existsByUserIdAndGroup_GroupCodeAndActiveTrue(String userId, String groupCode);
}
