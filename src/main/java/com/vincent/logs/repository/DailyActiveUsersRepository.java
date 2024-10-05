package com.vincent.logs.repository;

import com.vincent.logs.entity.DailyActiveUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyActiveUsersRepository extends JpaRepository<DailyActiveUsers, Long> {

}
