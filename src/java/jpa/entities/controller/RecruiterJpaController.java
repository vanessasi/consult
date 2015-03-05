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
import jpa.entities.Cliente;
import jpa.entities.Consultant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.entities.Recruiter;
import jpa.entities.controller.exceptions.IllegalOrphanException;
import jpa.entities.controller.exceptions.NonexistentEntityException;
import jpa.entities.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author vanessa.costa
 */
public class RecruiterJpaController implements Serializable {

    public RecruiterJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Recruiter recruiter) throws PreexistingEntityException, Exception {
        if (recruiter.getConsultantCollection() == null) {
            recruiter.setConsultantCollection(new ArrayList<Consultant>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente cliente = recruiter.getCliente();
            if (cliente != null) {
                cliente = em.getReference(cliente.getClass(), cliente.getClientePK());
                recruiter.setCliente(cliente);
            }
            Collection<Consultant> attachedConsultantCollection = new ArrayList<Consultant>();
            for (Consultant consultantCollectionConsultantToAttach : recruiter.getConsultantCollection()) {
                consultantCollectionConsultantToAttach = em.getReference(consultantCollectionConsultantToAttach.getClass(), consultantCollectionConsultantToAttach.getConsultantId());
                attachedConsultantCollection.add(consultantCollectionConsultantToAttach);
            }
            recruiter.setConsultantCollection(attachedConsultantCollection);
            em.persist(recruiter);
            if (cliente != null) {
                cliente.getRecruiterCollection().add(recruiter);
                cliente = em.merge(cliente);
            }
            for (Consultant consultantCollectionConsultant : recruiter.getConsultantCollection()) {
                Recruiter oldRecruiterIdConsOfConsultantCollectionConsultant = consultantCollectionConsultant.getRecruiterIdCons();
                consultantCollectionConsultant.setRecruiterIdCons(recruiter);
                consultantCollectionConsultant = em.merge(consultantCollectionConsultant);
                if (oldRecruiterIdConsOfConsultantCollectionConsultant != null) {
                    oldRecruiterIdConsOfConsultantCollectionConsultant.getConsultantCollection().remove(consultantCollectionConsultant);
                    oldRecruiterIdConsOfConsultantCollectionConsultant = em.merge(oldRecruiterIdConsOfConsultantCollectionConsultant);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRecruiter(recruiter.getRecruiterId()) != null) {
                throw new PreexistingEntityException("Recruiter " + recruiter + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Recruiter recruiter) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Recruiter persistentRecruiter = em.find(Recruiter.class, recruiter.getRecruiterId());
            Cliente clienteOld = persistentRecruiter.getCliente();
            Cliente clienteNew = recruiter.getCliente();
            Collection<Consultant> consultantCollectionOld = persistentRecruiter.getConsultantCollection();
            Collection<Consultant> consultantCollectionNew = recruiter.getConsultantCollection();
            List<String> illegalOrphanMessages = null;
            for (Consultant consultantCollectionOldConsultant : consultantCollectionOld) {
                if (!consultantCollectionNew.contains(consultantCollectionOldConsultant)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Consultant " + consultantCollectionOldConsultant + " since its recruiterIdCons field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (clienteNew != null) {
                clienteNew = em.getReference(clienteNew.getClass(), clienteNew.getClientePK());
                recruiter.setCliente(clienteNew);
            }
            Collection<Consultant> attachedConsultantCollectionNew = new ArrayList<Consultant>();
            for (Consultant consultantCollectionNewConsultantToAttach : consultantCollectionNew) {
                consultantCollectionNewConsultantToAttach = em.getReference(consultantCollectionNewConsultantToAttach.getClass(), consultantCollectionNewConsultantToAttach.getConsultantId());
                attachedConsultantCollectionNew.add(consultantCollectionNewConsultantToAttach);
            }
            consultantCollectionNew = attachedConsultantCollectionNew;
            recruiter.setConsultantCollection(consultantCollectionNew);
            recruiter = em.merge(recruiter);
            if (clienteOld != null && !clienteOld.equals(clienteNew)) {
                clienteOld.getRecruiterCollection().remove(recruiter);
                clienteOld = em.merge(clienteOld);
            }
            if (clienteNew != null && !clienteNew.equals(clienteOld)) {
                clienteNew.getRecruiterCollection().add(recruiter);
                clienteNew = em.merge(clienteNew);
            }
            for (Consultant consultantCollectionNewConsultant : consultantCollectionNew) {
                if (!consultantCollectionOld.contains(consultantCollectionNewConsultant)) {
                    Recruiter oldRecruiterIdConsOfConsultantCollectionNewConsultant = consultantCollectionNewConsultant.getRecruiterIdCons();
                    consultantCollectionNewConsultant.setRecruiterIdCons(recruiter);
                    consultantCollectionNewConsultant = em.merge(consultantCollectionNewConsultant);
                    if (oldRecruiterIdConsOfConsultantCollectionNewConsultant != null && !oldRecruiterIdConsOfConsultantCollectionNewConsultant.equals(recruiter)) {
                        oldRecruiterIdConsOfConsultantCollectionNewConsultant.getConsultantCollection().remove(consultantCollectionNewConsultant);
                        oldRecruiterIdConsOfConsultantCollectionNewConsultant = em.merge(oldRecruiterIdConsOfConsultantCollectionNewConsultant);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = recruiter.getRecruiterId();
                if (findRecruiter(id) == null) {
                    throw new NonexistentEntityException("The recruiter with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Recruiter recruiter;
            try {
                recruiter = em.getReference(Recruiter.class, id);
                recruiter.getRecruiterId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The recruiter with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Consultant> consultantCollectionOrphanCheck = recruiter.getConsultantCollection();
            for (Consultant consultantCollectionOrphanCheckConsultant : consultantCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Recruiter (" + recruiter + ") cannot be destroyed since the Consultant " + consultantCollectionOrphanCheckConsultant + " in its consultantCollection field has a non-nullable recruiterIdCons field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente cliente = recruiter.getCliente();
            if (cliente != null) {
                cliente.getRecruiterCollection().remove(recruiter);
                cliente = em.merge(cliente);
            }
            em.remove(recruiter);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Recruiter> findRecruiterEntities() {
        return findRecruiterEntities(true, -1, -1);
    }

    public List<Recruiter> findRecruiterEntities(int maxResults, int firstResult) {
        return findRecruiterEntities(false, maxResults, firstResult);
    }

    private List<Recruiter> findRecruiterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Recruiter.class));
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

    public Recruiter findRecruiter(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Recruiter.class, id);
        } finally {
            em.close();
        }
    }

    public int getRecruiterCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Recruiter> rt = cq.from(Recruiter.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
