package de.hi.ai.logic.web.webservice;

import de.hi.ai.logic.firstorder.exceptions.FormulaException;
import de.hi.ai.logic.firstorder.formation.Formel;
import de.hi.ai.logic.firstorder.util.ClassUtil;
import de.hi.ai.logic.web.logapp.TransformationException;
import de.hi.ai.logic.web.logapp.TransformationHelper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author Hakan
 */
@Named
@ApplicationScoped
@Path(value = "/transform/{transformation}")
public class ManticBean implements Serializable{
	
	private static Logger log = Logger.getLogger(ManticBean.class);

    @Inject
    TransformationHelper transformator;
    
	@PostConstruct	
    public void constructed(){
    	log.debug("created...");
    }
    
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Consumes({MediaType.TEXT_PLAIN,MediaType.APPLICATION_FORM_URLENCODED})
    public String getTransformated(@PathParam("transformation") String transformation, InputStream is,@Context HttpServletRequest req,@Context UriInfo info){
        Formel formula;
        try {
        	log.debug(req.getContentType());
        	List<Formel> formels = new ArrayList<Formel>();
        	if(req.getContentType().startsWith(MediaType.APPLICATION_FORM_URLENCODED)){
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    line = br.readLine();
                    String[] lineArgs = line.split("&");
                    if(lineArgs.length>0){
                        for (int i = 0; i < lineArgs.length; i++) {
                            String[] pair = lineArgs[i].split("=");
                            if(pair.length==2){
                                String value = pair[1];
                                String formulaDecoded = URLDecoder.decode(value,"utf-8");
                                log.debug(formulaDecoded);
                                formels.add(ClassUtil.parse(formulaDecoded));
                            }
                        }
                    }
        	}
        	else if(req.getContentType().startsWith(MediaType.TEXT_PLAIN)){
                    InputStreamReader isr = new InputStreamReader(is,"utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    String formelString="";
                    while((line=br.readLine())!=null){
                        formelString += line;
                    }
                    formels.add(ClassUtil.parse(formelString));
        	}
            String result = handle(transformation,formels.toArray(new Formel[formels.size()]));
            return result;
        } catch (FormulaException ex) {
            return ex.getMessage();
        }
        catch(Exception ex){
            return ex.getMessage();
        }
    }

	private String handle(String transformation,Formel... formula) {
        try {
            Object result = transformator.transform(transformation, formula);
            return result.toString();
        } catch (TransformationException ex) {
            return ex.getMessage();
        }
        catch(Exception ex){
            return ex.getMessage();
        }
    }
    
}
