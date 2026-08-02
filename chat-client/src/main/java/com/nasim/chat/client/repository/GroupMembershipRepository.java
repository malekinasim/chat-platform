package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {

    boolean existsByUserIdAndGroup_GroupCodeAndActiveTrue(String userId, String groupCode);

    @Query(value = """ 
            select  gm from GroupMembership gm
            inner join gm.ChatGroup g
            where gm.userId= :userId
            
            """)
    List<GroupMembership> findUserAllActiveGroup(@Param(value = "userID") String userId);
}
