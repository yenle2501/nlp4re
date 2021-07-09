import React,{Component} from 'react';
import axios from 'axios';
import './AddRules.css';
import {Button, Form, Row, Col, Modal, Tooltip, OverlayTrigger} from 'react-bootstrap';
import Draggable from 'react-draggable';
import 'bootstrap/dist/css/bootstrap.min.css';

export default class AddRules extends Component {
 
    constructor(props){
	   super(props);
	   this.state = {
			  addRules: this.props.onSetAddRules,
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
			  isHoveringConditions: false,
			  isHoveringDetails: false,
			  isHoveringModalVerb: false,
			  isHoveringObjects: false,
			  isHoveringSystemName: false,
			  isHoveringActivities : false,
			  rule: ''
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
	    this.toggleHoverState = this.toggleHoverState.bind(this);
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
	    
	 toggleHoverState = (value, stateName) => {
		 switch(stateName){
		 	 case "anchor":
				 this.setState({ isHoveringActivities: value  });
		 		 break;
			 case "conditions":
				 this.setState({ isHoveringConditions: value  });
				 break;
			 case "details":
				 this.setState({ isHoveringDetails: value  });
				 break;
			 case "modal":
				 this.setState({ isHoveringModalVerb: value  });
				 break;
			 case "objects":
				 this.setState({ isHoveringObjects: value  });
				 break;
			 case "systemName":
				 this.setState({ isHoveringSystemName: value  });
				 break;
			 default:
				 break;
		 }
          this.loadRules(stateName);
     }
        
    loadRules=(stateName) =>{ 
   	    var tmp = '';
    	axios.get("http://localhost:8080/description/getRules")
        .then(response => {
        	 if(response.data != null) {
        		 Object.keys(response.data).forEach(function(key) {      
        		
        			 if(key===stateName){
        				var listRules = response.data[key];
        				listRules.forEach(function(obj) {
        					if(key ==="modal"){
        						tmp = [tmp, <div> <label > {obj.key_name}  </label>	</div>];
        					}
        					else {
	        					tmp = [tmp, <div> <label > {obj.regex}  </label> </div> ];
        					}
        				});
        				return;
        			}
        		 });
        		 this.setState({rule: tmp });
        	 }
        	 else {
        		 console.log(response.status);
        	 }
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
    	const preCondition     = {key_name:"precondition", regex: this.state.preCondition, required : this.state.cBPreCondition===true ? 0 : 1}
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

    	axios.post("http://localhost:8080/description/addRules", params)
        .then(response => {
        	  if(response.status === 201){
        		  this.setConfirmation(true);
        	  }
        })
    }
        
    render() {
    	return (
    		<Draggable  disabled={false} active="true">
    	        <div className="AddRules">
    	            <Button className="close" variant="success" onClick={()=> this.props.onSetAddRules(false)} >
    	            	x
    	            </Button>
    	            <div>
    	            	<h2>ADD RULES</h2>
    	            </div>
    	            <div className="content">
			    	  <Form>
			    	    <Form.Group  value={this.state.preCondition}  onChange={this.setPreCondition}>
					    	 <OverlayTrigger placement="right" delay={{ show: 250, hide: 400 }}	overlay={ <Tooltip>	{this.state.rule} </Tooltip>}>
					    	    <Form.Label onMouseEnter={() =>this.toggleHoverState(true,"conditions")} onMouseLeave={() =>this.toggleHoverState(false,"conditions")}>Precondition:</Form.Label>
			    	        </OverlayTrigger>
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
			    	     	<OverlayTrigger placement="right" delay={{ show: 250, hide: 400 }}	overlay={ <Tooltip>	{this.state.rule} </Tooltip>}>
			    	     		<Form.Label onMouseEnter={() =>this.toggleHoverState(true,"systemName")} onMouseLeave={() =>this.toggleHoverState(false,"systemName")}>System Determiner: </Form.Label>
			    	     	</OverlayTrigger>
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
			    	      	  <OverlayTrigger placement="right" delay={{ show: 250, hide: 400 }}	overlay={ <Tooltip>	{this.state.rule} </Tooltip>}>
			    	      	  		<Form.Label onMouseEnter={() =>this.toggleHoverState(true,"modal")} onMouseLeave={() =>this.toggleHoverState(false,"modal")}>Modalverb: </Form.Label>
			    	      	  </OverlayTrigger>
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
	    	              	  <OverlayTrigger placement="right" delay={{ show: 250, hide: 400 }}	overlay={ <Tooltip>	{this.state.rule} </Tooltip>}>
	    	              	  		<Form.Label onMouseEnter={() =>this.toggleHoverState(true,"anchor")} onMouseLeave={() =>this.toggleHoverState(false,"anchor")}>Processword: </Form.Label>
	    	              	  </OverlayTrigger>
				    	      <Row>
					          	<Col sm={9}> 
		    	              		<Form.Control placeholder="e.g. BE ABLE TO " />  
		    	             	 </Col>
							     <Col sm={1}>
							         <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBProcessWord : event.target.checked})}}/>
							     </Col>
							  </Row>
		    	          </Form.Group>
	    	              
	    	              <Form.Group value={this.state.objectName}  onChange={this.setObjectName}>
	    	              	  <OverlayTrigger placement="right" delay={{ show: 250, hide: 400 }}	overlay={ <Tooltip>	{this.state.rule} </Tooltip>}>
	    	              	  		<Form.Label onMouseEnter={() =>this.toggleHoverState(true,"objects")} onMouseLeave={() =>this.toggleHoverState(false,"objects")}>Object: </Form.Label>
	    	              	  </OverlayTrigger>
				    	      <Row>
					          	<Col sm={9}> 	
					          		<Form.Control placeholder="e.g. A +" />
					          	</Col>
								<Col sm={1}>
								     <Form.Check type="checkbox" label="optinal" onChange={(event)=>{this.setState({cBObjectName : event.target.checked})}}/>
								</Col>
							  </Row>
		    	          </Form.Group>
	    	              
	    	              <Form.Group value={this.state.postCondition}  onChange={this.setPostCondition}>
	    	                  <OverlayTrigger placement="right" delay={{ show: 250, hide: 400 }}	overlay={ <Tooltip>	{this.state.rule} </Tooltip>}>
	    	                  		<Form.Label onMouseEnter={() =>this.toggleHoverState(true,"details")} onMouseLeave={() =>this.toggleHoverState(false,"details")}>Postcondition: </Form.Label>
	    	                 </OverlayTrigger>
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
                		<Button variant="success" onClick={()=> this.props.onSetAddRules(false)} > Cancel </Button>
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
      		    	<Modal show={true} onHide={()=> this.props.onSetAddRules(false)} variant="success" animation={true}>
      				    <Modal.Header >
      				      <Modal.Title>Success</Modal.Title>
      				     </Modal.Header>
      				     <Modal.Body>
      				   Fields were added.
      				      </Modal.Body>
      				      <Modal.Footer>
      				         <Button variant="success" onClick={()=> this.props.onSetAddRules(false)}>
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