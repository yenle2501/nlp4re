import React,{Component} from 'react';
import './PopUp.css';
import {Button} from 'react-bootstrap';
import Draggable from 'react-draggable';
import { MDBContainer} from "mdbreact";
import 'bootstrap/dist/css/bootstrap.min.css';

export default class PopUp extends Component {
 
    constructor(props){
	   super(props);
	   this.state = {
	          popUpChange: this.props.onSetPopUp,
	          content    : this.props.onSetContent
	        }
	}

    
    render() {
    	return (
    		<Draggable  disabled={false} active="true">
    	        <div className="PopUp">
    	            <Button className="popup-close" variant="danger" onClick={()=> this.props.onSetPopUp(false)} >
    	            	X
    	            </Button>
    	            <h2>CONFORMAL REQUIREMENTS</h2>
    	            <div className="pu-content-container">
    	            	<MDBContainer className="scrollbar scrollbar-primary">
    	            		{this.state.content}
                		</MDBContainer>
                	</div>
               </div>
            </Draggable>
    	    );
    }
}