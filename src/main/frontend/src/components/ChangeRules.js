import React,{Component} from 'react';
import axios from 'axios';
import './ChangeRules.css';
import {Button, Form, Row, Col, Modal} from 'react-bootstrap';
import Draggable from 'react-draggable';
import 'bootstrap/dist/css/bootstrap.min.css';

export default class ChangeRules extends Component {
 
    constructor(props){
	   super(props);
	   this.state = {
			  changeRules: this.props.onSetChangeRules,
			  alert: false,
			  confirmation : false,
			  preCondition: '',
			  systemDeterminer:'',
			  modalVerb:'',
			  processWord:'',
			  objectName:'',
			  postCondition:'',
			  cBPreCondition : false,
			  cBSystemDeterminer : false,
			  cBModalVerb : false,
			  cBProcessWord : false,
			  cBObjectName : false,
			  cBPostCondition : false,
	        }

	    this.saveRules = this.saveRules.bind(this);
	    this.showAlert = this.showAlert.bind(this);
	    this.setConfirmation = this.setConfirmation.bind(this);
	    this.setPreCondition  = this.setPreCondition.bind(this);
	    this.setSystemDeterminer  = this.setSystemDeterminer.bind(this);
	    this.setModalVerb  = this.setModalVerb.bind(this);
	    this.setProcessWord  = this.setProcessWord.bind(this);
	    this.setPostCondition  = this.setPostCondition.bind(this);
	    this.setObjectName = this.setObjectName.bind(this);
    
    }
        
    showAlert = (value) => {
   	  this.setState({ alert: value  });
   }
   
    setConfirmation= (value) => {
   	  this.setState({ confirmation: value });
   }
    
    setPreCondition=(event) => {
    	 this.setState({
      		  preCondition: event.target.value
   		   });
    }
    
    setSystemDeterminer=(event) => {
   	 this.setState({
   		systemDeterminer: event.target.value
  		   });
   }
    
    setModalVerb=(event) => {
      	 this.setState({
      		modalVerb: event.target.value
     		   });
      }
    
    setProcessWord=(event) => {
      	 this.setState({
      		processWord: event.target.value
     		   });
      }
    
    setPostCondition=(event) => {
      	 this.setState({
      		postCondition: event.target.value
     		   });
      }
    
    setObjectName=(event) => {
      	 this.setState({
      		objectName: event.target.value
     		   });
      }
    
    saveRules=() =>{    	
    	// check input
    	if( (this.state.preCondition === null || this.state.preCondition === "")
    		&& (this.state.systemDeterminer === null || this.state.systemDeterminer === "")
    		&& (this.state.modalVerb === null || this.state.modalVerb === "")
    		&& (this.state.postCondition === null || this.state.postCondition === "")
    		&& (this.state.processWord === null || this.state.processWord === "")
    		&& (this.state.objectName === null || this.state.objectName === "")){
    		
        		this.showAlert(true);
        		return;
        }
       	
    	// not required = 0, required= 1
    	const preCondition     = {key_name: "precondition", regex: this.state.preCondition, required : this.state.cBPreCondition===true ? 0 : 1}
    	const systemDeterminer = {key_name:"systemName" , regex: this.state.systemDeterminer, required : this.state.cBSystemDeterminer===true ? 0 : 1}
    	const modalVerb        = {key_name : this.state.modalVerb, required : this.state.cBModalVerb===true ? 0 : 1}
    	const postCondition    = {key_name:"postCondition" , regex: this.state.postCondition, required : this.state.cBPostCondition===true ? 0 : 1}
    	const processWord      = {key_name:"processWord" , regex: this.state.processWord, required : this.state.cBProcessWord===true ? 0 : 1}
    	const objectName       = {key_name:"objectName" , regex: this.state.objectName, required : this.state.cBObjectName===true ? 0 : 1}
    	      
    	 const params ={
    		 conditions: preCondition,
    		 systemName: systemDeterminer,
    		 modal     : modalVerb,
    		 object    : objectName,
    		 anchor    : processWord,
    		 details   : postCondition
           };

    	axios.post("http://localhost:8080/description/changeRules", params)
        .then(response => {
        	  if(response.status === 201){
        		  this.setConfirmation(true);
        	  }
        })
    }

    
    render() {
    	return (
    		<Draggable  disabled={false} active="true">
    	        <div className="ChangeRules">
    	            <Button className="close" variant="success" onClick={()=> this.props.onSetChangeRules(false)} >
    	            	x
    	            </Button>
    	            <div>
    	            	<h2>CHANGE RULES</h2>
    	            </div>
    	            <div className="content">
			    	  <Form>
			    	    <Form.Group  value={this.state.preCondition}  onChange={this.setPreCondition}>
			    	        <Form.Label>Change Precondition:</Form.Label>
			    	        <Row>
				    	        <Col sm={9}> 
			    	        		<Form.Control placeholder="e.g. ^WHILE + ," />
				    	        </Col>
				    	        <Col sm={1}>
				    	              <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBPreCondition : event.target.checked})}}/>
				    	         </Col>
			    	         </Row>
			    	     </Form.Group>
			    	   
			    	     <Form.Group value={this.state.systemDeterminer}  onChange={this.setSystemDeterminer}>
			    	     	<Form.Label>Change System Determiner: </Form.Label>
			    	        <Row>
					          <Col sm={9}> 
			    	              <Form.Control placeholder="e.g. THE +" />
			    	       	  </Col>
					          <Col sm={1}>
					               <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBSystemDeterminer : event.target.checked})}}/>
					          </Col>
				    	    </Row>
			    	      </Form.Group>
			    	      
			    	      <Form.Group value={this.state.modalVerb}  onChange={this.setModalVerb}>
		    	              <Form.Label>Add Modalverb: </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 
					          		<Form.Control placeholder="e.g. MUST" />
					             </Col>
							     <Col sm={1}>
							         <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBModalVerb : event.target.checked})}}/>
							     </Col>
						      </Row>
					      </Form.Group>
	    	              
	    	              <Form.Group value={this.state.processWord}  onChange={this.setProcessWord}>
		    	              <Form.Label>Change Processword: </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 
		    	              		<Form.Control placeholder="e.g. PROVIDE [\w\s] THE ABILITY TO [\w]" />  
		    	             	 </Col>
							     <Col sm={1}>
							         <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBProcessWord : event.target.checked})}}/>
							     </Col>
							  </Row>
		    	          </Form.Group>
	    	              
	    	              <Form.Group value={this.state.objectName}  onChange={this.setObjectName}>
		    	              <Form.Label>Change Object: </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 	
					          		<Form.Control placeholder="e.g. A +" />
					          	</Col>
								<Col sm={1}>
								     <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBObjectName : event.target.checked})}}/>
								</Col>
							  </Row>
		    	          </Form.Group>
	    	              
	    	              <Form.Group value={this.state.postCondition}  onChange={this.setPostCondition}
	    	              >
		    	              <Form.Label>Change Postcondition: </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 
					          		<Form.Control placeholder="e.g. IF AND ONLY IF +" />
					          	</Col>
							    <Col sm={1}>
							         <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBPostCondition : event.target.checked})}}/>
							    </Col>
							  </Row>		    	              				
	    	              </Form.Group>     
			    	              
			        </Form>
		 	     </div>
                 <Row>
                   	<Col>
                   		<Button variant="success" onClick={this.saveRules} > Save </Button>
                	</Col>
                	<Col>
                		<Button variant="success" onClick={()=> this.props.onSetChangeRules(false)} > Cancel </Button>
                	</Col>
                 </Row>
                 	  
                 {this.state.alert &&
     		    	<Modal show={true} onHide={() =>this.showAlert(false)} variant="danger" animation={true}>
     				    <Modal.Header >
     				      <Modal.Title>Warning</Modal.Title>
     				     </Modal.Header>
     				     <Modal.Body>
     				        Please fill out fields
     				      </Modal.Body>
     				      <Modal.Footer>
     				         <Button variant="danger" onClick={() =>this.showAlert(false)}>
     				            Close
     				         </Button>
     				      </Modal.Footer>
     				</Modal>
     		      }
                 
                 {this.state.confirmation &&
      		    	<Modal show={true} onHide={()=> this.props.onSetChangeRules(false)} variant="success" animation={true}>
      				    <Modal.Header >
      				      <Modal.Title>Success</Modal.Title>
      				     </Modal.Header>
      				     <Modal.Body>
      				   Fields were changed/ added.
      				      </Modal.Body>
      				      <Modal.Footer>
      				         <Button variant="success" onClick={()=> this.props.onSetChangeRules(false)}>
      				            OK
      				         </Button>
      				      </Modal.Footer>
      				</Modal>
      		      }
                 
               </div>
            </Draggable>
    	    );
    }
}