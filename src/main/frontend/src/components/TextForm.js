import React from 'react';
import './TextForm/TextForm.css';
import axios from 'axios';
import PopUp from './PopUp.js';

export default class TextForm extends React.Component {

	
	constructor(props) {
	    super(props);
	    this.state = {description : '',
	    			  showPopUp : false};
	    this.handleChange =  this.handleChange.bind(this);
	    this.checkText =  this.checkText.bind(this);
	  }

	
	//function
	handleChange(event){
		this.setState({
			description: event.target.value
		    });
	}
	
	checkText(event){
		event.preventDefault();
		const desc = {description : this.state.description}
		
		console.log('desc:' + this.state.description)
		
		// send data to backend
		axios.put("http://localhost:8080/description/test", desc)
            .then(response => {
                if(response.data != null) {
                	console.log(response.status)
                } else {
                	console.log(response.status)
                }
            });
		
		 console.log("after send request");
		 this.setState({
			 showPopUp : true
		 });
		 
	}
			  
	  render() {
		  return (
		  <form onSubmit={this.checkText}>
	      		<label> Requirement description </label>
	      		<textarea id="text" name="typeText" alue={this.state.description}  	onChange={this.handleChange}  placeholder=" Write requirements.."/>
	      		<button type="submit"> Check </button>
	      </form>
	      );
	  }
	  
}

