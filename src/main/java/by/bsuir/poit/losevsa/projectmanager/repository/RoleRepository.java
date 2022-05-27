package by.bsuir.poit.losevsa.projectmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.bsuir.poit.losevsa.projectmanager.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role getReferenceByName(String name);
}