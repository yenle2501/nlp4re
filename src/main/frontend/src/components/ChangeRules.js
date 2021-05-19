import React,{Component} from 'react';
import './ChangeRules.css';
import {Button, Form, Row, Col} from 'react-bootstrap';
import Draggable from 'react-draggable';
import 'bootstrap/dist/css/bootstrap.min.css';

export default class ChangeRules extends Component {
 
    constructor(props){
	   super(props);
	   this.state = {
			  changeRules: this.props.onSetChangeRules,
	        }
	}
    
    

    
    render() {
    	return (
    		<Draggable  disabled={false} active="true">
    	        <div className="ChangeRules">
    	            <Button className="close" variant="success" onClick={()=> this.props.onSetChangeRules(false)} >
    	            	X
    	            </Button>
    	            <div>
    	            	<h2>Change Rules</h2>
    	            </div>
    	            <div className="content">
			    	  <Form>
			    	    <Form.Group controlId="formPrecondition">
			    	        <Form.Label>Change Precondition</Form.Label>
			    	        <Row>
				    	        <Col sm={9}> 
				    	        	<Form.Control placeholder="Enter Precondition" />
				    	        </Col>
				    	        <Col sm={1}>
				    	              <Form.Check type="checkbox" label="optinal" />
				    	         </Col>
			    	         </Row>
			    	     </Form.Group>
			    	   
			    	     <Form.Group controlId="formSystemname">
			    	     	<Form.Label>Change System Determiner </Form.Label>
			    	        <Row>
					          <Col sm={9}> 
			    	              <Form.Control placeholder="System Determiner" />
			    	       	  </Col>
					          <Col sm={1}>
					               <Form.Check type="checkbox" label="optinal" />
					          </Col>
				    	    </Row>
			    	      </Form.Group>
			    	      
			    	      <Form.Group controlId="formModalVerb">
		    	              <Form.Label>Add Modalverb </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 
					          		<Form.Control placeholder="Modalverb" />
					             </Col>
							     <Col sm={1}>
							         <Form.Check type="checkbox" label="optinal" />
							     </Col>
						      </Row>
					      </Form.Group>
	    	              
	    	              <Form.Group controlId="formProcessword">
		    	              <Form.Label>Change Processword </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 
		    	              		<Form.Control placeholder="Processword" />  
		    	             	 </Col>
							     <Col sm={1}>
							         <Form.Check type="checkbox" label="optinal" />
							     </Col>
							  </Row>
		    	          </Form.Group>
	    	              
	    	              <Form.Group controlId="formObject">
		    	              <Form.Label>Change Object </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 	
					          		<Form.Control placeholder="Object" />
					          	</Col>
								<Col sm={1}>
								     <Form.Check type="checkbox" label="optinal" />
								</Col>
							  </Row>
		    	          </Form.Group>
	    	              
	    	              <Form.Group controlId="formPostCondition">
		    	              <Form.Label>Change Postcondition </Form.Label>
		    	              <Row>
					          	<Col sm={9}> 
					          		<Form.Control placeholder="Postcondition" />
					          	</Col>
							    <Col sm={1}>
							         <Form.Check type="checkbox" label="optinal" />
							    </Col>
							  </Row>		    	              				
	    	              </Form.Group>     
			    	              
			        </Form>
		 	     </div>
                   <Row>
                		<Button variant="success" onClick={()=> this.props.onSetChangeRules(false)} > Save </Button>
                 	</Row>
               </div>
            </Draggable>
    	    );
    }
}