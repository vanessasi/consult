/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jpa.entities.Consultant;
import jpa.entities.ProjectConsultant;
import jpa.entities.ProjectConsultantPK;
import jpa.entities.controller.exceptions.NonexistentEntityException;
import jpa.entities.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author vanessa.costa
 */
public class ProjectConsultantJpaController implements Serializable {

    public ProjectConsultantJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProjectConsultant projectConsultant) throws PreexistingEntityException, Exception {
        if (projectConsultant.getProjectConsultantPK() == null) {
            projectConsultant.setProjectConsultantPK(new ProjectConsultantPK());
        }
        projectConsultant.getProjectConsultantPK().setConsultantIdProj(projectConsultant.getConsultant().getConsultantId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Consultant consultant = projectConsultant.getConsultant();
            if (consultant != null) {
                consultant = em.getReference(consultant.getClass(), consultant.getConsultantId());
                projectConsultant.setConsultant(consultant);
            }
            em.persist(projectConsultant);
            if (consultant != null) {
                consultant.getProjectConsultantCollection().add(projectConsultant);
                consultant = em.merge(consultant);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProjectConsultant(projectConsultant.getProjectConsultantPK()) != null) {
                throw new PreexistingEntityException("ProjectConsultant " + projectConsultant + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProjectConsultant projectConsultant) throws NonexistentEntityException, Exception {
        projectConsultant.getProjectConsultantPK().setConsultantIdProj(projectConsultant.getConsultant().getConsultantId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectConsultant persistentProjectConsultant = em.find(ProjectConsultant.class, projectConsultant.getProjectConsultantPK());
            Consultant consultantOld = persistentProjectConsultant.getConsultant();
            Consultant consultantNew = projectConsultant.getConsultant();
            if (consultantNew != null) {
                consultantNew = em.getReference(consultantNew.getClass(), consultantNew.getConsultantId());
                projectConsultant.setConsultant(consultantNew);
            }
            projectConsultant = em.merge(projectConsultant);
            if (consultantOld != null && !consultantOld.equals(consultantNew)) {
                consultantOld.getProjectConsultantCollection().remove(projectConsultant);
                consultantOld = em.merge(consultantOld);
            }
            if (consultantNew != null && !consultantNew.equals(consultantOld)) {
                consultantNew.getProjectConsultantCollection().add(projectConsultant);
                consultantNew = em.merge(consultantNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ProjectConsultantPK id = projectConsultant.getProjectConsultantPK();
                if (findProjectConsultant(id) == null) {
                    throw new NonexistentEntityException("The projectConsultant with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ProjectConsultantPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProjectConsultant projectConsultant;
            try {
                projectConsultant = em.getReference(ProjectConsultant.class, id);
                projectConsultant.getProjectConsultantPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The projectConsultant with id " + id + " no longer exists.", enfe);
            }
            Consultant consultant = projectConsultant.getConsultant();
            if (consultant != null) {
                consultant.getProjectConsultantCollection().remove(projectConsultant);
                consultant = em.merge(consultant);
            }
            em.remove(projectConsultant);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProjectConsultant> findProjectConsultantEntities() {
        return findProjectConsultantEntities(true, -1, -1);
    }

    public List<ProjectConsultant> findProjectConsultantEntities(int maxResults, int firstResult) {
        return findProjectConsultantEntities(false, maxResults, firstResult);
    }

    private List<ProjectConsultant> findProjectConsultantEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProjectConsultant.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public ProjectConsultant findProjectConsultant(ProjectConsultantPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProjectConsultant.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectConsultantCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProjectConsultant> rt = cq.from(ProjectConsultant.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
