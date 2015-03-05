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
import jpa.entities.Billable;
import jpa.entities.Project;
import jpa.entities.Consultant;
import jpa.entities.controller.exceptions.NonexistentEntityException;
import jpa.entities.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author vanessa.costa
 */
public class BillableJpaController implements Serializable {

    public BillableJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Billable billable) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project project = billable.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getProjectPK());
                billable.setProject(project);
            }
            Consultant consultantIdBi = billable.getConsultantIdBi();
            if (consultantIdBi != null) {
                consultantIdBi = em.getReference(consultantIdBi.getClass(), consultantIdBi.getConsultantId());
                billable.setConsultantIdBi(consultantIdBi);
            }
            em.persist(billable);
            if (project != null) {
                project.getBillableCollection().add(billable);
                project = em.merge(project);
            }
            if (consultantIdBi != null) {
                consultantIdBi.getBillableCollection().add(billable);
                consultantIdBi = em.merge(consultantIdBi);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBillable(billable.getBillableId()) != null) {
                throw new PreexistingEntityException("Billable " + billable + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Billable billable) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Billable persistentBillable = em.find(Billable.class, billable.getBillableId());
            Project projectOld = persistentBillable.getProject();
            Project projectNew = billable.getProject();
            Consultant consultantIdBiOld = persistentBillable.getConsultantIdBi();
            Consultant consultantIdBiNew = billable.getConsultantIdBi();
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getProjectPK());
                billable.setProject(projectNew);
            }
            if (consultantIdBiNew != null) {
                consultantIdBiNew = em.getReference(consultantIdBiNew.getClass(), consultantIdBiNew.getConsultantId());
                billable.setConsultantIdBi(consultantIdBiNew);
            }
            billable = em.merge(billable);
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getBillableCollection().remove(billable);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getBillableCollection().add(billable);
                projectNew = em.merge(projectNew);
            }
            if (consultantIdBiOld != null && !consultantIdBiOld.equals(consultantIdBiNew)) {
                consultantIdBiOld.getBillableCollection().remove(billable);
                consultantIdBiOld = em.merge(consultantIdBiOld);
            }
            if (consultantIdBiNew != null && !consultantIdBiNew.equals(consultantIdBiOld)) {
                consultantIdBiNew.getBillableCollection().add(billable);
                consultantIdBiNew = em.merge(consultantIdBiNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = billable.getBillableId();
                if (findBillable(id) == null) {
                    throw new NonexistentEntityException("The billable with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Billable billable;
            try {
                billable = em.getReference(Billable.class, id);
                billable.getBillableId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The billable with id " + id + " no longer exists.", enfe);
            }
            Project project = billable.getProject();
            if (project != null) {
                project.getBillableCollection().remove(billable);
                project = em.merge(project);
            }
            Consultant consultantIdBi = billable.getConsultantIdBi();
            if (consultantIdBi != null) {
                consultantIdBi.getBillableCollection().remove(billable);
                consultantIdBi = em.merge(consultantIdBi);
            }
            em.remove(billable);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Billable> findBillableEntities() {
        return findBillableEntities(true, -1, -1);
    }

    public List<Billable> findBillableEntities(int maxResults, int firstResult) {
        return findBillableEntities(false, maxResults, firstResult);
    }

    private List<Billable> findBillableEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Billable.class));
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

    public Billable findBillable(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Billable.class, id);
        } finally {
            em.close();
        }
    }

    public int getBillableCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Billable> rt = cq.from(Billable.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
