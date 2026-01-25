//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v3.0.0 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2026.01.25 à 02:57:54 AM CET 
//


package com.cyterafle.salahsama.claim.processing.soap.generated;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="found" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="policy" type="{http://cyterafle.com/claim/soap/policy}policyInfo" minOccurs="0"/&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "found",
    "policy",
    "message"
})
@XmlRootElement(name = "getPolicyDetailsResponse")
public class GetPolicyDetailsResponse {

    protected boolean found;
    protected PolicyInfo policy;
    @XmlElement(required = true)
    protected String message;

    /**
     * Obtient la valeur de la propriété found.
     * 
     */
    public boolean isFound() {
        return found;
    }

    /**
     * Définit la valeur de la propriété found.
     * 
     */
    public void setFound(boolean value) {
        this.found = value;
    }

    /**
     * Obtient la valeur de la propriété policy.
     * 
     * @return
     *     possible object is
     *     {@link PolicyInfo }
     *     
     */
    public PolicyInfo getPolicy() {
        return policy;
    }

    /**
     * Définit la valeur de la propriété policy.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyInfo }
     *     
     */
    public void setPolicy(PolicyInfo value) {
        this.policy = value;
    }

    /**
     * Obtient la valeur de la propriété message.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Définit la valeur de la propriété message.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

}
