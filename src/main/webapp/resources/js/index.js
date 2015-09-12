// stops bubbling from commandbutton to pannelgroup
$(function(){
    jQuery('#inputForm\\:g1 *').on(
        'click',
        function(){
            event.stopPropagation();
        }
                );
    }
);
var focusedField = "formula";
window.onload = function(){
    setFocus(document.getElementById("inputForm:focusid").value);
}
function setFocus(input){
    if(typeof input !== 'undefined'){
        focusedField = input;
    }
    var elem = document.getElementById("inputForm:"+focusedField);
    elem.focus();
    document.getElementById("inputForm:focusid").value=input;
    if(focusedField === 'formula'){
        var selectionStart1 = document.getElementById("inputForm:selectionStart1");
        selectionStart1.value = elem.selectionStart;
    }
    else if(focusedField === 'formula2'){
        var selectionStart2 = document.getElementById("inputForm:selectionStart2");
        selectionStart2.value = elem.selectionStart;
    }
}
