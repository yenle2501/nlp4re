import React, {Component} from 'react';
import axios from 'axios';
import './TextForm.css';
import PopUp from './PopUp';
import ChangeRules from './ChangeRules';
import {Accordion,Button, Card, Modal, Form} from 'react-bootstrap';


export default class TextForm  extends Component {
	
	// constructor
	constructor(props) {
	    super(props);
	    
	    this.state = {
				popUp: false,
				description: '',
				result: '',
				alert: false,
				changeRules: false
		}
	    
	    this.handleOnChange =  this.handleOnChange.bind(this);
	    this.setResult = this.setResult.bind(this);
	    this.setDescription = this.setDescription.bind(this);
	    this.setPopUp = this.setPopUp.bind(this);
	    this.setChangeRules = this.setChangeRules.bind(this);
	    this.checkText =  this.checkText.bind(this);
	    this.changeRules =  this.changeRules.bind(this);
	  }
	
	// functions
	setResult =(value) => {
		 this.setState({
			 result: value
		    });
	 }
	
	
	setDescription =(value) => {
		 this.setState({
			 description: value
		    });
	 }
	 
	 setPopUp =(value) => {
		 this.setState({
				popUp: value
		    });
	 }
	   
	 setChangeRules =(value) => {
		 this.setState({
			 changeRules: value
		    });
	 }
	 
     handleOnChange =(event) => {
    	 this.setState({
 				description: event.target.value
 		    });
    }
     
    showAlert = (value) => {
    	 this.setState({
			 alert: value
		    });
    }
         
    checkText=() =>{
    	// check input
    	if(this.state.description === null || this.state.description === "") {
    		this.showAlert(true);
    		return;
    	}
    	
		// in json format {descrition: content}
		const desc = {description: this.state.description}
		// send data to backend and receive response
			
		axios.put("http://localhost:8080/description/check", desc)
            .then(response => {
                if(response.data != null) {
        	    	var resullt = response.data[0];
        	    	var conform_list = response.data[1];
        	    	var logs_list = response.data[2];
        	    	
        	    	var tmp = '';
        	    	
        	    	Object.keys(resullt).forEach(function(key) {        	    		
        	    		// conform
        	    		if(conform_list[key] === '0'){
        	    			tmp = [tmp, <Card style={{ backgroundColor: '#90ee90' }}> 
        	    							{resullt[key]}
        	    						</Card>
        	    				  ];
        	    		}
        	    		// not conform
        	    		else if(conform_list[key] === '1'){
        	    			tmp = [tmp, 
        	    				<Accordion  defaultActiveKey="0" >
	        	    			  <Card>
	        	    			      <Accordion.Toggle as={Card.Header} variant="link" eventKey="1" style={{ backgroundColor: ' #ff5050' }}>
	        	    			      	  {resullt[key]}
	        	    			      </Accordion.Toggle>
	        	    			      <Accordion.Collapse eventKey="1" style={{ backgroundColor: '#ffb2b2' }}>
		        	    			      <Card.Body>
		        	    			      	<label>Logs:</label>
		        	    			      	<div>
		        	    			      		{logs_list[key]}
		        	    			      	</div>
		        	    			      </Card.Body>
	        	    			     </Accordion.Collapse>
	        	    			  </Card>
        	    			</Accordion>
        	    				 
        	    				];
        	    		}
            		});
        	    	
        	    	
        	    	this.setResult(tmp);
                	this.setPopUp(true);
                } else {
                
                	console.log(response.status)
                }
            });
	    }
    
 
    changeRules=() =>{
    	this.setChangeRules(true)
    }
    
	    render() {
	    	return (
	            <div className={"TextForm"}>
		            <div className="head"> 
		                <h1>Requirements Description</h1> 
		            </div> 
		            <div className="check">        
					   <Form>
						   <Form.Group  value={this.state.description}  onChange={this.handleOnChange}>
						     <Form.Control as="textarea" rows={15} />
						   </Form.Group>
					    </Form>
					   <Button variant="success" onClick={this.checkText} >Check</Button>
					   <Button variant="success" onClick={this.changeRules} >Change Rules</Button>
				      {this.state.alert &&
				    	  	<Modal show={true} onHide={() =>this.showAlert(false)} variant="danger" animation={true}>
						        <Modal.Header >
						          <Modal.Title>Warning</Modal.Title>
						        </Modal.Header>
						        <Modal.Body>
						        	Please fill out the field 
						        </Modal.Body>
						        <Modal.Footer>
						          <Button variant="danger" onClick={() =>this.showAlert(false)}>
						            Close
						          </Button>
						        </Modal.Footer>
						    </Modal>
				      }
				     </div>
		            {this.state.popUp && <PopUp onSetPopUp={this.setPopUp} onSetContent={this.state.result} />}
		            {this.state.changeRules && <ChangeRules onSetChangeRules={this.setChangeRules}  />}
	           </div>
	    	);
	    }
}
