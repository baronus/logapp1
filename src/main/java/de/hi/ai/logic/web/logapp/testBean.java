/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hi.ai.logic.web.logapp;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 *
 * @author Hakan
 */
@Named(value = "testBean")
@Dependent
public class testBean {
    
    private String testString = "testString";

    /**
     * Creates a new instance of testBean
     */
    public testBean() {
    }
    
    public void setTestString(String s){
        this.testString = s;
    }
    public String getTestString(){
        return this.testString;
    }
}
