/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jpa.entities.Consultant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.entities.ConsultantStatus;
import jpa.entities.controller.exceptions.IllegalOrphanException;
import jpa.entities.controller.exceptions.NonexistentEntityException;
import jpa.entities.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author vanessa.costa
 */
public class ConsultantStatusJpaController implements Serializable {

    public ConsultantStatusJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ConsultantStatus consultantStatus) throws PreexistingEntityException, Exception {
        if (consultantStatus.getConsultantCollection() == null) {
            consultantStatus.setConsultantCollection(new ArrayList<Consultant>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Consultant> attachedConsultantCollection = new ArrayList<Consultant>();
            for (Consultant consultantCollectionConsultantToAttach : consultantStatus.getConsultantCollection()) {
                consultantCollectionConsultantToAttach = em.getReference(consultantCollectionConsultantToAttach.getClass(), consultantCollectionConsultantToAttach.getConsultantId());
                attachedConsultantCollection.add(consultantCollectionConsultantToAttach);
            }
            consultantStatus.setConsultantCollection(attachedConsultantCollection);
            em.persist(consultantStatus);
            for (Consultant consultantCollectionConsultant : consultantStatus.getConsultantCollection()) {
                ConsultantStatus oldStatusIdOfConsultantCollectionConsultant = consultantCollectionConsultant.getStatusId();
                consultantCollectionConsultant.setStatusId(consultantStatus);
                consultantCollectionConsultant = em.merge(consultantCollectionConsultant);
                if (oldStatusIdOfConsultantCollectionConsultant != null) {
                    oldStatusIdOfConsultantCollectionConsultant.getConsultantCollection().remove(consultantCollectionConsultant);
                    oldStatusIdOfConsultantCollectionConsultant = em.merge(oldStatusIdOfConsultantCollectionConsultant);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findConsultantStatus(consultantStatus.getStatusId()) != null) {
                throw new PreexistingEntityException("ConsultantStatus " + consultantStatus + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ConsultantStatus consultantStatus) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ConsultantStatus persistentConsultantStatus = em.find(ConsultantStatus.class, consultantStatus.getStatusId());
            Collection<Consultant> consultantCollectionOld = persistentConsultantStatus.getConsultantCollection();
            Collection<Consultant> consultantCollectionNew = consultantStatus.getConsultantCollection();
            List<String> illegalOrphanMessages = null;
            for (Consultant consultantCollectionOldConsultant : consultantCollectionOld) {
                if (!consultantCollectionNew.contains(consultantCollectionOldConsultant)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Consultant " + consultantCollectionOldConsultant + " since its statusId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Consultant> attachedConsultantCollectionNew = new ArrayList<Consultant>();
            for (Consultant consultantCollectionNewConsultantToAttach : consultantCollectionNew) {
                consultantCollectionNewConsultantToAttach = em.getReference(consultantCollectionNewConsultantToAttach.getClass(), consultantCollectionNewConsultantToAttach.getConsultantId());
                attachedConsultantCollectionNew.add(consultantCollectionNewConsultantToAttach);
            }
            consultantCollectionNew = attachedConsultantCollectionNew;
            consultantStatus.setConsultantCollection(consultantCollectionNew);
            consultantStatus = em.merge(consultantStatus);
            for (Consultant consultantCollectionNewConsultant : consultantCollectionNew) {
                if (!consultantCollectionOld.contains(consultantCollectionNewConsultant)) {
                    ConsultantStatus oldStatusIdOfConsultantCollectionNewConsultant = consultantCollectionNewConsultant.getStatusId();
                    consultantCollectionNewConsultant.setStatusId(consultantStatus);
                    consultantCollectionNewConsultant = em.merge(consultantCollectionNewConsultant);
                    if (oldStatusIdOfConsultantCollectionNewConsultant != null && !oldStatusIdOfConsultantCollectionNewConsultant.equals(consultantStatus)) {
                        oldStatusIdOfConsultantCollectionNewConsultant.getConsultantCollection().remove(consultantCollectionNewConsultant);
                        oldStatusIdOfConsultantCollectionNewConsultant = em.merge(oldStatusIdOfConsultantCollectionNewConsultant);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Character id = consultantStatus.getStatusId();
                if (findConsultantStatus(id) == null) {
                    throw new NonexistentEntityException("The consultantStatus with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Character id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ConsultantStatus consultantStatus;
            try {
                consultantStatus = em.getReference(ConsultantStatus.class, id);
                consultantStatus.getStatusId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The consultantStatus with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Consultant> consultantCollectionOrphanCheck = consultantStatus.getConsultantCollection();
            for (Consultant consultantCollectionOrphanCheckConsultant : consultantCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ConsultantStatus (" + consultantStatus + ") cannot be destroyed since the Consultant " + consultantCollectionOrphanCheckConsultant + " in its consultantCollection field has a non-nullable statusId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(consultantStatus);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ConsultantStatus> findConsultantStatusEntities() {
        return findConsultantStatusEntities(true, -1, -1);
    }

    public List<ConsultantStatus> findConsultantStatusEntities(int maxResults, int firstResult) {
        return findConsultantStatusEntities(false, maxResults, firstResult);
    }

    private List<ConsultantStatus> findConsultantStatusEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ConsultantStatus.class));
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

    public ConsultantStatus findConsultantStatus(Character id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ConsultantStatus.class, id);
        } finally {
            em.close();
        }
    }

    public int getConsultantStatusCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ConsultantStatus> rt = cq.from(ConsultantStatus.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
