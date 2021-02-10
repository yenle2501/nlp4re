import React,{Component} from 'react';
import './PopUp/PopUp.css';
import {Button} from 'react-bootstrap';
import Draggable from 'react-draggable';
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
    	            <Button className="popup-close" variant="light" onClick={()=> this.props.onSetPopUp(false)} >
    	            	X
    	            </Button>
    	            <h2>Compliant requirements</h2>
    	            <div className="pu-content-container">
                		{this.state.content}
                	</div>
               </div>
            </Draggable>
    	    );
    }
}