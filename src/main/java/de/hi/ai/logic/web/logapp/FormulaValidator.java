/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hi.ai.logic.web.logapp;

import de.hi.ai.logic.firstorder.exceptions.FormulaException;
import de.hi.ai.logic.firstorder.formation.Formel;
import de.hi.ai.logic.firstorder.util.ClassUtil;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Hakan
 */

@FacesValidator(value = "logic.formula.Validator")
public class FormulaValidator implements Validator{
    
    private static Logger log = LogManager.getLogger(FormulaValidator.class);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        try {
            if(component.getAttributes()!=null){
                Object immi = component.getAttributes().get("immi");
                if(immi!=null){
                    component.getAttributes().remove("immi");
                    return;
                }
            }
            Formel f = ClassUtil.parse(String.valueOf(value).trim());
            context.getAttributes().put(component.getId(), f);
        } catch (FormulaException ex) {
            context.getAttributes().put(component.getId(), Formel.newInstanceEmpty());
            FacesMessage fm = new FacesMessage("Error: "+ex.getMessage());
            context.addMessage("inputForm:"+component.getId(), fm);
        }
        catch(Exception ex2){
            ex2.printStackTrace();
            log.debug("the exception:"+ex2.getMessage());
            context.getAttributes().put(component.getId(), Formel.newInstanceEmpty());
            FacesMessage fm = new FacesMessage("Error: Syntax");
            context.addMessage("inputForm:"+component.getId(), fm);
        }
    }
    
}
