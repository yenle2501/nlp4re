import React from 'react';
import './PopUp/PopUp.css';

export default class PopUp extends React.Component {

	constructor(props) {
	    super(props);
	  }

	
	// function
	  render() {
		  return (
				  <div className="wrap">
		          	<div>
		          <button className="closeBtn">
		            Hide
		            </button>
		          </div>
		        </div>
			);
	  }
	  
}

