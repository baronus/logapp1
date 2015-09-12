/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hi.ai.logic.web.logapp;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;

import org.apache.log4j.Logger;

import de.hi.ai.logic.firstorder.formation.Formel;
import de.hi.ai.logic.model.manifest.Function;
import de.hi.ai.logic.model.manifest.Relation;
import de.hi.ai.model.manifest.Variable;
import de.hi.ai.model.symbol.logic.Sign;

/**
 *
 * @author Hakan
 */
@Named(value = "formulaBean")
@SessionScoped
public class FormulaBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(FormulaBean.class);
    
    private String formulaString = "";
    private String formulaString2 = "";
    private Formel formula = Formel.newInstanceEmpty();
    private Formel formula2 = Formel.newInstanceEmpty();
    private String focusid = "formula";
    private String selectionStart1 = "0";
    private String selectionStart2 = "0";
    boolean selected1 = false;
    boolean selected2 = false;

    @PostConstruct
    public void constructed(){
    	log.debug("bean constructed...");
    }
    
    
    public String getFocusid() {
        return focusid;
    }
    
    public void setFocusid(String focusid){
        this.focusid = focusid;
    }
    /**
     * Creates a new instance of FormulaBean
     */
    public FormulaBean() {
    	log.debug("formulabean created...");
    }
    
    public void reset(AjaxBehaviorEvent abe){
        formula = Formel.newInstanceEmpty();
        formulaString = "";
        selectionStart1 = "0";
    }
    
    public void parse(AjaxBehaviorEvent abe){
        this.formula = (Formel)FacesContext.getCurrentInstance().getAttributes().get("formula");
        FacesMessage fm = new FacesMessage("Parsed Formula: " +this.formula);
        FacesContext.getCurrentInstance().addMessage("inputForm:formula", fm);
    }
    public void reset2(){
        formula2 = Formel.newInstanceEmpty();
        formulaString2 = "";
        selectionStart2 = "0";
    }
    
    public void parse2(){
        this.formula2 = (Formel)FacesContext.getCurrentInstance().getAttributes().get("formula2");
        FacesMessage fm = new FacesMessage("Parsed Formula2: " +this.formula2);
        FacesContext.getCurrentInstance().addMessage("inputForm:formula2", fm);
    }

    public String getSelectionStart1() {
        return selectionStart1;
    }

    public void setSelectionStart1(String selectionStart1) {
        this.selectionStart1 = selectionStart1;
    }

    public String getSelectionStart2() {
        return selectionStart2;
    }

    public void setSelectionStart2(String selectionStart2) {
        this.selectionStart2 = selectionStart2;
    }
    public String getFormula() {
        return formulaString;
    }
    public void setFormula(String formulaString){
        this.formulaString = formulaString;
    }
    public String getFormula2() {
        return formulaString2;
    }
    public void setFormula2(String formulaString){
        this.formulaString2 = formulaString;
    }
    
    public void setF(Formel f){
        this.formula = f;
    }

    public Formel getF(){
        return this.formula;
    }
    
    public Formel getF2(){
        return this.formula2;
    }
    
    private enum Operation{
        unknown,
        predicate,
        function,
        variable,
        negate,
        conjunction,
        disjunction,
        implication,
        bikonditional,
        openParenthesis,
        closeParenthesis,
        comma,
        forAll,
        exists,
        identic;
        public static Operation getForId(String id){
            try{
                return Operation.valueOf(id);
            }
            catch(IllegalArgumentException iae){
                return Operation.unknown;
            }
        }
    }
    public void keyup(AjaxBehaviorEvent abe){
        log.info(abe.getComponent().getId());
    }
    public void clickFormula(AjaxBehaviorEvent abe){
        focusid = abe.getComponent().getId();
        abe.getComponent().getAttributes().put("immi", true);
        Object sel1 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("inputForm:selectionStart1");
        selectionStart1 = sel1.toString();
        selected1 = true;
    }
    public void clickFormula2(AjaxBehaviorEvent abe){
        focusid = abe.getComponent().getId();
        abe.getComponent().getAttributes().put("immi", true);
        selectionStart2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("inputForm:selectionStart2");
        selected2 = true;
    }
    public void operate(AjaxBehaviorEvent abe){
        String id = (String)abe.getComponent().getAttributes().get("id");
        Operation op = Operation.getForId(id);
        String addString = "";
        switch(op){
            case predicate:
                Relation r = Relation.getNext();
                addString += r.getName();
                break;
            case function:
                Function f = Function.getNext();
                addString += f.getName();
                break;
            case variable:
                Variable v = Variable.getNext();
                addString += " " +v.getName()+" ";
                break;
            case negate:
                addString += Sign.Negation.getName();
                break;
            case conjunction:
                addString += Sign.Konjunktion.getName();
                break;
            case disjunction:
                addString += Sign.Disjunktion.getName();
                break;
            case implication:
                addString += Sign.Implikation.getName();
                break;
            case bikonditional:
                addString += Sign.Bikonditional.getName();
                break;
            case openParenthesis:
                addString += Sign.BracketOn.getName();
                break;
            case closeParenthesis:
                addString += Sign.BracketOff.getName();
                break;
            case comma:
                addString += Sign.Comma.getName();
                break;
            case forAll:
                addString += Sign.ForAll.getName();
                break;
            case exists:
                addString += Sign.Exists.getName();
                break;
            case identic:
                addString += Sign.Identic.getName();
                break;
            default:
                addString += op;
        }
        if(focusid.equals("formula")){
            if(selected1){
                formulaString = formulaString.substring(0, Integer.parseInt(selectionStart1))+
                    addString+
                formulaString.substring(Integer.parseInt(selectionStart1),formulaString.length());
                selected1 = false;
            }
            else{
                formulaString += addString;
            }
        }
        else if(focusid.equals("formula2")){
            if(selected2){
                formulaString2 = formulaString2.substring(0, Integer.parseInt(selectionStart2))+
                    addString+
                formulaString2.substring(Integer.parseInt(selectionStart2),formulaString2.length());
                selected2 = false;
            }
            else{
                formulaString2 += addString;
            }
        }
        UIComponent com = abe.getComponent();
        UIComponent parent = com.getParent();
        Object av = parent.getAttributes().get("att1");
        String attribute = "";
        if(av!=null){
            attribute = av.toString();
        }
        FacesMessage fm = new FacesMessage("summary2"+attribute);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }
}
