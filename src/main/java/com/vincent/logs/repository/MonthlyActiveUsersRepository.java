package com.vincent.logs.repository;

import com.vincent.logs.entity.MonthlyActiveUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyActiveUsersRepository extends JpaRepository<MonthlyActiveUsers, Long> {

}
