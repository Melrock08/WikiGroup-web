package com.wikigroup.demo.repositories;

import com.wikigroup.demo.models.ContactoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<ContactoEntity, Long> {
}
