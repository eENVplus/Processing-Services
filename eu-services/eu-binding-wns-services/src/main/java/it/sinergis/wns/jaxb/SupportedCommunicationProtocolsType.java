//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2014.06.04 alle 12:36:31 PM CEST 
//


package it.sinergis.wns.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SupportedCommunicationProtocolsType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SupportedCommunicationProtocolsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wns}ProtocolsType">
 *       &lt;sequence>
 *         &lt;element name="WSAddressing" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="WNS" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportedCommunicationProtocolsType", namespace = "http://www.opengis.net/wns", propOrder = {
    "wsAddressing",
    "wns"
})
public class SupportedCommunicationProtocolsType
    extends ProtocolsType
{

    @XmlElement(name = "WSAddressing")
    protected boolean wsAddressing;
    @XmlElement(name = "WNS")
    protected boolean wns;

    /**
     * Recupera il valore della proprietà wsAddressing.
     * 
     */
    public boolean isWSAddressing() {
        return wsAddressing;
    }

    /**
     * Imposta il valore della proprietà wsAddressing.
     * 
     */
    public void setWSAddressing(boolean value) {
        this.wsAddressing = value;
    }

    /**
     * Recupera il valore della proprietà wns.
     * 
     */
    public boolean isWNS() {
        return wns;
    }

    /**
     * Imposta il valore della proprietà wns.
     * 
     */
    public void setWNS(boolean value) {
        this.wns = value;
    }

}
