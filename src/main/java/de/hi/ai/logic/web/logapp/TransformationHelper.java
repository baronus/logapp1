/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hi.ai.logic.web.logapp;

import de.hi.ai.logic.firstorder.Rule;
import de.hi.ai.logic.firstorder.formation.Formel;
import de.hi.ai.logic.firstorder.rule.base.Distributivitaet;
import de.hi.ai.logic.firstorder.rule.transformation.Absorbtion;
import de.hi.ai.logic.firstorder.rule.transformation.Addition;
import de.hi.ai.logic.firstorder.rule.transformation.Equivalenz;
import de.hi.ai.logic.firstorder.rule.transformation.Konjunktion;
import de.hi.ai.logic.firstorder.rule.transformation.ModusBarbara;
import de.hi.ai.logic.firstorder.rule.transformation.ModusPonendoPonens;
import de.hi.ai.logic.firstorder.rule.transformation.ModusPonendoTolens;
import de.hi.ai.logic.firstorder.rule.transformation.ModusTolendoPonens;
import de.hi.ai.logic.firstorder.rule.transformation.ModusTolendoTolens;
import de.hi.ai.logic.firstorder.rule.transformation.ReductioAdAbsurdum;
import de.hi.ai.logic.firstorder.rule.transformation.Resolution;
import de.hi.ai.model.manifest.Variable;
import de.hi.ai.model.symbol.Symbol;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Hakan
 */
@Named
@ApplicationScoped
public class TransformationHelper implements Serializable{

	private List<String> msgs = new ArrayList<String>();
	
    public enum Transformations{
        addition,absorbtion,conjunction,
        modusBarbara,
        modusPonendoPonens,modusPonendoTolens,modusTolendoPonens,modusTolendoTolens,
        reductioAdAbsurdum,
        clearAssoziations,
        clearBiconditional,
        clearConditional,
        clearDoubleNegation,
        minimizeNegationSkopus,
        distribution,
        reformDistribution,
        innerDistribution,
        idempotenz,
        toOrdered,
        toKNF,
        cleanVariables,
        prenex,
        skolem,
        skolemWithPrenex,
        clausesset,
        unificator,
        resolution_sentential,
        resolution_firstorder;
    }
    public Object transform(String transformation, Formel... fi) throws TransformationException{
        Transformations t = Transformations.valueOf(transformation);
        Formel transformedFormula = Formel.newInstanceEmpty();
        if(fi.length<1){
        	return "needs minimum one formula to transform";
        }
        String errorMsg = "";
        try{
            switch(t){
                case addition:
                    transformedFormula = Addition.transform(fi[0], fi[1]);
                    break;
                case conjunction:
                    transformedFormula = Konjunktion.transform(fi[0], fi[1]);
                    break;
                case modusBarbara:
                    transformedFormula = ModusBarbara.transform(fi[0], fi[1]);
                    break;
                case modusPonendoPonens:
                    transformedFormula = ModusPonendoPonens.transform(fi[0], fi[1]);
                    break;
                case modusPonendoTolens:
                    transformedFormula = ModusPonendoTolens.transform(fi[0], fi[1]);
                    break;
                case modusTolendoPonens:
                    transformedFormula = ModusTolendoPonens.transform(fi[0], fi[1]);
                    break;
                case modusTolendoTolens:
                    transformedFormula = ModusTolendoTolens.transform(fi[0], fi[1]);
                    break;
                case absorbtion:
                    transformedFormula = Absorbtion.transform(fi[0]);
                    break;
                case reductioAdAbsurdum:
                    transformedFormula = ReductioAdAbsurdum.transform(fi[0]);
                    break;
                case clearAssoziations:
                    transformedFormula = Equivalenz.clearAssoziations(fi[0]);
                    break;
                case clearBiconditional:
                    transformedFormula = Equivalenz.clearBikonditional(fi[0]);
                    break;
                case clearConditional:
                    transformedFormula = Equivalenz.clearKonditional(fi[0]);
                    break;
                case clearDoubleNegation:
                    transformedFormula = Equivalenz.clearDoubleNegation(fi[0]);
                    break;
                case minimizeNegationSkopus:
                    transformedFormula = Equivalenz.minimizeNegationSkopus(fi[0]);
                    break;
                case distribution:
                    transformedFormula = Distributivitaet.newInstance().transform(fi[0]);
                    break;
                case reformDistribution:
                    transformedFormula = Distributivitaet.newInstance().reformation(fi[0]);
                    break;
                case innerDistribution:
                    transformedFormula = Equivalenz.innerDistribution(fi[0]);
                    break;
                case idempotenz:
                    transformedFormula = Equivalenz.transformIdempotenz(fi[0]);
                    break;
                case toOrdered:
                    transformedFormula = Equivalenz.transform2Ordered(fi[0]);
                    break;
                case toKNF:
                	transformedFormula = Equivalenz.transform2KNF(fi[0]);
                	break;
                case cleanVariables:
                    transformedFormula = fi[0].clean();
                    break;
                case prenex:
                    transformedFormula = Equivalenz.transform2Praenex(fi[0]);
                    break;
                case skolem:
                    transformedFormula = Equivalenz.transform2Skolem(fi[0]);
                    break;
                case skolemWithPrenex:
                    transformedFormula = Equivalenz.transform2Praenex(fi[0]);
                    transformedFormula = Equivalenz.transform2Skolem(transformedFormula);
                    break;
                case clausesset:
                	return fi[0].getKlauselSet();
                case unificator:
                    ArrayList<Map<Variable, Symbol>> unificated = Formel.getUnificator(fi[0]);
                    return unificated;
                case resolution_sentential:
                	if(fi.length==2){
                		transformedFormula = Resolution.transform(fi[0], fi[1]);
                	}
                	else{
                		return "the sentential resolution needs 2 formula for transformation!";
                	}
                    break;
                case resolution_firstorder:
                    try {
                        transformedFormula = Equivalenz.transform2Praenex(fi[0]);
                        transformedFormula = Equivalenz.transform2Skolem(transformedFormula);
                        HashSet<Formel> klauselSet = transformedFormula.getKlauselSet();
                        // test for max. 30 sec.
                        TimeUnit timeUnit = TimeUnit.SECONDS;
                        boolean isUnfulfillable = new Rule().isUnfulfillableWithResolution(klauselSet, 1L, timeUnit);
                        return "The formula is unfulfillable:"+isUnfulfillable;
                    } catch (Exception e) {
                        return "Internal Error, maybe time is gone!";
                    }
                default:
                    return errorMsg;
            }
        }
        catch(Exception ex){
            return ex.getMessage();
        }
        if(!transformedFormula.isEmpty()){
        	return transformedFormula;
        }
        else{
            return "Transformed formula is Empty: ";
        }
    }
}
