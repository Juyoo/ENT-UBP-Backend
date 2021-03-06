package org.ubp.ent.backend.core.dao.repository.teacher.contact.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ubp.ent.backend.core.domains.teacher.contact.email.EmailTypeDomain;

/**
 * Created by Anthony on 15/01/2016.
 */
public interface EmailTypeRepository extends JpaRepository<EmailTypeDomain, Long> {

}
