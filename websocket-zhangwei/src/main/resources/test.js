function SokcetMessageHandler(){
    this.alert = function(params){
        alert(params);
    };
    this.dispatch= function(funname, params) {
        this[funname](params);
    }
}

var handler = new SokcetMessageHandler();
handler.dispatch("alert", "123")