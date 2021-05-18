import React,{Component} from 'react';
import './ChangeRules.css';
import {Button} from 'react-bootstrap';
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
    	            <Button className="close" variant="danger" onClick={()=> this.props.onSetChangeRules(false)} >
    	            	X
    	            </Button>
    	            <h2>Add New Rule</h2>
    	            <div className="content">
                	</div>
                	 <Button variant="success" onClick={()=> this.props.onSetChangeRules(false)} > Save </Button>
               </div>
            </Draggable>
    	    );
    }
}