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
import jpa.entities.Address;
import jpa.entities.Recruiter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.entities.Cliente;
import jpa.entities.ClientePK;
import jpa.entities.controller.exceptions.IllegalOrphanException;
import jpa.entities.controller.exceptions.NonexistentEntityException;
import jpa.entities.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author vanessa.costa
 */
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) throws PreexistingEntityException, Exception {
        if (cliente.getClientePK() == null) {
            cliente.setClientePK(new ClientePK());
        }
        if (cliente.getRecruiterCollection() == null) {
            cliente.setRecruiterCollection(new ArrayList<Recruiter>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address billingAddress = cliente.getBillingAddress();
            if (billingAddress != null) {
                billingAddress = em.getReference(billingAddress.getClass(), billingAddress.getAddressId());
                cliente.setBillingAddress(billingAddress);
            }
            Collection<Recruiter> attachedRecruiterCollection = new ArrayList<Recruiter>();
            for (Recruiter recruiterCollectionRecruiterToAttach : cliente.getRecruiterCollection()) {
                recruiterCollectionRecruiterToAttach = em.getReference(recruiterCollectionRecruiterToAttach.getClass(), recruiterCollectionRecruiterToAttach.getRecruiterId());
                attachedRecruiterCollection.add(recruiterCollectionRecruiterToAttach);
            }
            cliente.setRecruiterCollection(attachedRecruiterCollection);
            em.persist(cliente);
            if (billingAddress != null) {
                billingAddress.getClienteCollection().add(cliente);
                billingAddress = em.merge(billingAddress);
            }
            for (Recruiter recruiterCollectionRecruiter : cliente.getRecruiterCollection()) {
                Cliente oldClienteOfRecruiterCollectionRecruiter = recruiterCollectionRecruiter.getCliente();
                recruiterCollectionRecruiter.setCliente(cliente);
                recruiterCollectionRecruiter = em.merge(recruiterCollectionRecruiter);
                if (oldClienteOfRecruiterCollectionRecruiter != null) {
                    oldClienteOfRecruiterCollectionRecruiter.getRecruiterCollection().remove(recruiterCollectionRecruiter);
                    oldClienteOfRecruiterCollectionRecruiter = em.merge(oldClienteOfRecruiterCollectionRecruiter);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCliente(cliente.getClientePK()) != null) {
                throw new PreexistingEntityException("Cliente " + cliente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getClientePK());
            Address billingAddressOld = persistentCliente.getBillingAddress();
            Address billingAddressNew = cliente.getBillingAddress();
            Collection<Recruiter> recruiterCollectionOld = persistentCliente.getRecruiterCollection();
            Collection<Recruiter> recruiterCollectionNew = cliente.getRecruiterCollection();
            List<String> illegalOrphanMessages = null;
            for (Recruiter recruiterCollectionOldRecruiter : recruiterCollectionOld) {
                if (!recruiterCollectionNew.contains(recruiterCollectionOldRecruiter)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Recruiter " + recruiterCollectionOldRecruiter + " since its cliente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (billingAddressNew != null) {
                billingAddressNew = em.getReference(billingAddressNew.getClass(), billingAddressNew.getAddressId());
                cliente.setBillingAddress(billingAddressNew);
            }
            Collection<Recruiter> attachedRecruiterCollectionNew = new ArrayList<Recruiter>();
            for (Recruiter recruiterCollectionNewRecruiterToAttach : recruiterCollectionNew) {
                recruiterCollectionNewRecruiterToAttach = em.getReference(recruiterCollectionNewRecruiterToAttach.getClass(), recruiterCollectionNewRecruiterToAttach.getRecruiterId());
                attachedRecruiterCollectionNew.add(recruiterCollectionNewRecruiterToAttach);
            }
            recruiterCollectionNew = attachedRecruiterCollectionNew;
            cliente.setRecruiterCollection(recruiterCollectionNew);
            cliente = em.merge(cliente);
            if (billingAddressOld != null && !billingAddressOld.equals(billingAddressNew)) {
                billingAddressOld.getClienteCollection().remove(cliente);
                billingAddressOld = em.merge(billingAddressOld);
            }
            if (billingAddressNew != null && !billingAddressNew.equals(billingAddressOld)) {
                billingAddressNew.getClienteCollection().add(cliente);
                billingAddressNew = em.merge(billingAddressNew);
            }
            for (Recruiter recruiterCollectionNewRecruiter : recruiterCollectionNew) {
                if (!recruiterCollectionOld.contains(recruiterCollectionNewRecruiter)) {
                    Cliente oldClienteOfRecruiterCollectionNewRecruiter = recruiterCollectionNewRecruiter.getCliente();
                    recruiterCollectionNewRecruiter.setCliente(cliente);
                    recruiterCollectionNewRecruiter = em.merge(recruiterCollectionNewRecruiter);
                    if (oldClienteOfRecruiterCollectionNewRecruiter != null && !oldClienteOfRecruiterCollectionNewRecruiter.equals(cliente)) {
                        oldClienteOfRecruiterCollectionNewRecruiter.getRecruiterCollection().remove(recruiterCollectionNewRecruiter);
                        oldClienteOfRecruiterCollectionNewRecruiter = em.merge(oldClienteOfRecruiterCollectionNewRecruiter);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ClientePK id = cliente.getClientePK();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ClientePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getClientePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Recruiter> recruiterCollectionOrphanCheck = cliente.getRecruiterCollection();
            for (Recruiter recruiterCollectionOrphanCheckRecruiter : recruiterCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Recruiter " + recruiterCollectionOrphanCheckRecruiter + " in its recruiterCollection field has a non-nullable cliente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Address billingAddress = cliente.getBillingAddress();
            if (billingAddress != null) {
                billingAddress.getClienteCollection().remove(cliente);
                billingAddress = em.merge(billingAddress);
            }
            em.remove(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(ClientePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
