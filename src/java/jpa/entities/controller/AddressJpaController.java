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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.entities.Address;
import jpa.entities.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author vanessa.costa
 */
public class AddressJpaController implements Serializable {

    public AddressJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Address address) {
        if (address.getClienteCollection() == null) {
            address.setClienteCollection(new ArrayList<Cliente>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Cliente> attachedClienteCollection = new ArrayList<Cliente>();
            for (Cliente clienteCollectionClienteToAttach : address.getClienteCollection()) {
                clienteCollectionClienteToAttach = em.getReference(clienteCollectionClienteToAttach.getClass(), clienteCollectionClienteToAttach.getClientePK());
                attachedClienteCollection.add(clienteCollectionClienteToAttach);
            }
            address.setClienteCollection(attachedClienteCollection);
            em.persist(address);
            for (Cliente clienteCollectionCliente : address.getClienteCollection()) {
                Address oldBillingAddressOfClienteCollectionCliente = clienteCollectionCliente.getBillingAddress();
                clienteCollectionCliente.setBillingAddress(address);
                clienteCollectionCliente = em.merge(clienteCollectionCliente);
                if (oldBillingAddressOfClienteCollectionCliente != null) {
                    oldBillingAddressOfClienteCollectionCliente.getClienteCollection().remove(clienteCollectionCliente);
                    oldBillingAddressOfClienteCollectionCliente = em.merge(oldBillingAddressOfClienteCollectionCliente);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Address address) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address persistentAddress = em.find(Address.class, address.getAddressId());
            Collection<Cliente> clienteCollectionOld = persistentAddress.getClienteCollection();
            Collection<Cliente> clienteCollectionNew = address.getClienteCollection();
            Collection<Cliente> attachedClienteCollectionNew = new ArrayList<Cliente>();
            for (Cliente clienteCollectionNewClienteToAttach : clienteCollectionNew) {
                clienteCollectionNewClienteToAttach = em.getReference(clienteCollectionNewClienteToAttach.getClass(), clienteCollectionNewClienteToAttach.getClientePK());
                attachedClienteCollectionNew.add(clienteCollectionNewClienteToAttach);
            }
            clienteCollectionNew = attachedClienteCollectionNew;
            address.setClienteCollection(clienteCollectionNew);
            address = em.merge(address);
            for (Cliente clienteCollectionOldCliente : clienteCollectionOld) {
                if (!clienteCollectionNew.contains(clienteCollectionOldCliente)) {
                    clienteCollectionOldCliente.setBillingAddress(null);
                    clienteCollectionOldCliente = em.merge(clienteCollectionOldCliente);
                }
            }
            for (Cliente clienteCollectionNewCliente : clienteCollectionNew) {
                if (!clienteCollectionOld.contains(clienteCollectionNewCliente)) {
                    Address oldBillingAddressOfClienteCollectionNewCliente = clienteCollectionNewCliente.getBillingAddress();
                    clienteCollectionNewCliente.setBillingAddress(address);
                    clienteCollectionNewCliente = em.merge(clienteCollectionNewCliente);
                    if (oldBillingAddressOfClienteCollectionNewCliente != null && !oldBillingAddressOfClienteCollectionNewCliente.equals(address)) {
                        oldBillingAddressOfClienteCollectionNewCliente.getClienteCollection().remove(clienteCollectionNewCliente);
                        oldBillingAddressOfClienteCollectionNewCliente = em.merge(oldBillingAddressOfClienteCollectionNewCliente);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = address.getAddressId();
                if (findAddress(id) == null) {
                    throw new NonexistentEntityException("The address with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address address;
            try {
                address = em.getReference(Address.class, id);
                address.getAddressId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The address with id " + id + " no longer exists.", enfe);
            }
            Collection<Cliente> clienteCollection = address.getClienteCollection();
            for (Cliente clienteCollectionCliente : clienteCollection) {
                clienteCollectionCliente.setBillingAddress(null);
                clienteCollectionCliente = em.merge(clienteCollectionCliente);
            }
            em.remove(address);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Address> findAddressEntities() {
        return findAddressEntities(true, -1, -1);
    }

    public List<Address> findAddressEntities(int maxResults, int firstResult) {
        return findAddressEntities(false, maxResults, firstResult);
    }

    private List<Address> findAddressEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Address.class));
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

    public Address findAddress(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Address.class, id);
        } finally {
            em.close();
        }
    }

    public int getAddressCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Address> rt = cq.from(Address.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
