package de.hi.ai.logic.web.logapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import de.hi.ai.logic.firstorder.formation.Formel;
import de.hi.ai.logic.web.logapp.TransformationHelper.Transformations;
import de.hi.ai.model.manifest.Variable;
import de.hi.ai.model.symbol.Symbol;

/**
 *
 * @author Hakan
 */
@Named(value = "transformationBean")
@RequestScoped
public class TransformationBean implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(TransformationBean.class);

    @Inject
    private FormulaBean formulaBean;
    @Inject
    private TransformationHelper transformationsHelper;
    
    
    /**
     * Creates a new instance of TransformationBean
     */
    public TransformationBean() {
    	log.debug("transformationBean created...");
    }
    
    public void transform(String transformation){
    	TransformationHelper.Transformations t = Transformations.valueOf(transformation);
        FacesMessage fm = new FacesMessage("No Transformation defined for: "+t);
        Object transformed = null;
        try {
			transformed =  transformationsHelper.transform(transformation, formulaBean.getF(),formulaBean.getF2());
		} catch (TransformationException e1) {
			e1.printStackTrace();
			fm = new FacesMessage("Transformation error:"+e1.getMessage());
		}
        switch(t){
            case unificator:
                ArrayList<Map<Variable, Symbol>> unificated = (ArrayList<Map<Variable,Symbol>>)transformed;
                if(unificated==null || unificated.isEmpty()){
                    fm = new FacesMessage("not unificable");
                }
                else{
                    fm = new FacesMessage("unificable!");
                }
                FacesContext.getCurrentInstance().addMessage(null, fm);
                return;
            case resolution_firstorder:
            	String resultMsg = (String) transformed;
            	fm = new FacesMessage(resultMsg);
                FacesContext.getCurrentInstance().addMessage(null, fm);
                return;
            default:
        }
        if(transformed instanceof Formel){
        	Formel transformedFormula = (Formel)transformed;
	        if(!transformedFormula.isEmpty()){
	            if(!formulaBean.getF().equals(transformedFormula)){
	                formulaBean.setF(transformedFormula);
	                fm = new FacesMessage("Transformed formula by "+t.name()+": " +transformedFormula);
	            }
	            else{
	                fm = new FacesMessage("No Transformation available");
	            }
	        }
	        else{
	            fm = new FacesMessage("Transformed formula is Empty: ");
	        }
        }
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }

	public TransformationHelper getTransformationsHelper() {
		return transformationsHelper;
	}

	public void setTransformationsHelper(TransformationHelper transformationsHelper) {
		this.transformationsHelper = transformationsHelper;
	}
}
