package com.backend.evently.repository;

import com.backend.evently.model.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizadorRepository extends JpaRepository<Organizador, UUID> {
}
