//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2014.06.04 alle 12:36:31 PM CEST 
//


package it.sinergis.wns.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per UpdateMultiUserRegistrationType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="UpdateMultiUserRegistrationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wns}BaseOperationType">
 *       &lt;sequence>
 *         &lt;element name="MultiUserID" type="{http://www.opengis.net/wns}UserIDType"/>
 *         &lt;element name="addUser" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ID" type="{http://www.opengis.net/wns}UserIDType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="removeUser" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ID" type="{http://www.opengis.net/wns}UserIDType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateMultiUserRegistrationType", namespace = "http://www.opengis.net/wns", propOrder = {
    "multiUserID",
    "addUser",
    "removeUser"
})
public class UpdateMultiUserRegistrationType
    extends BaseOperationType
{

    @XmlElement(name = "MultiUserID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String multiUserID;
    protected UpdateMultiUserRegistrationType.AddUser addUser;
    protected UpdateMultiUserRegistrationType.RemoveUser removeUser;

    /**
     * Recupera il valore della proprietà multiUserID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMultiUserID() {
        return multiUserID;
    }

    /**
     * Imposta il valore della proprietà multiUserID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMultiUserID(String value) {
        this.multiUserID = value;
    }

    /**
     * Recupera il valore della proprietà addUser.
     * 
     * @return
     *     possible object is
     *     {@link UpdateMultiUserRegistrationType.AddUser }
     *     
     */
    public UpdateMultiUserRegistrationType.AddUser getAddUser() {
        return addUser;
    }

    /**
     * Imposta il valore della proprietà addUser.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateMultiUserRegistrationType.AddUser }
     *     
     */
    public void setAddUser(UpdateMultiUserRegistrationType.AddUser value) {
        this.addUser = value;
    }

    /**
     * Recupera il valore della proprietà removeUser.
     * 
     * @return
     *     possible object is
     *     {@link UpdateMultiUserRegistrationType.RemoveUser }
     *     
     */
    public UpdateMultiUserRegistrationType.RemoveUser getRemoveUser() {
        return removeUser;
    }

    /**
     * Imposta il valore della proprietà removeUser.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateMultiUserRegistrationType.RemoveUser }
     *     
     */
    public void setRemoveUser(UpdateMultiUserRegistrationType.RemoveUser value) {
        this.removeUser = value;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ID" type="{http://www.opengis.net/wns}UserIDType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "id"
    })
    public static class AddUser {

        @XmlElement(name = "ID", namespace = "http://www.opengis.net/wns", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected List<String> id;

        /**
         * Gets the value of the id property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the id property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getID() {
            if (id == null) {
                id = new ArrayList<String>();
            }
            return this.id;
        }

    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ID" type="{http://www.opengis.net/wns}UserIDType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "id"
    })
    public static class RemoveUser {

        @XmlElement(name = "ID", namespace = "http://www.opengis.net/wns", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected List<String> id;

        /**
         * Gets the value of the id property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the id property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getID() {
            if (id == null) {
                id = new ArrayList<String>();
            }
            return this.id;
        }

    }

}
