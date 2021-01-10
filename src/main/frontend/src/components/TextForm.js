import React, {useState} from 'react';
import axios from 'axios';
import './TextForm/TextForm.css';
import PopUp from './PopUp';

const TextForm = () => {
    // controls if popup displays
    const [popUp, setPopUp] = useState(false)
    const [description,setDescription] = useState('')
    
    // adds class to darken background color
//    const duringPopUp = popUp ? " during-popup" : ""
    
    let handleChange =(event) =>setDescription(event.target.value);
    
    let checkText=() =>{
    	setPopUp(true)
		console.log('desc:' + description)
		// in json format {descrition: content}
		const desc = {description: description}
		// send data to backend
	
		axios.put("http://localhost:8080/description/check", desc)
            .then(response => {
                if(response.data != null) {
                	
                	console.log(response.status)
                } else {
                	console.log(response.status)
                }
            });
		
		 console.log("after send request");
    }
    
    
    return (
        <div className={"TextForm"}>
            <div className="head"> 
                <h1>Requirements Description</h1> 
            </div>
	        <div className={"check"}>            
		         <label>  Description </label>
				 <textarea value={description}  onChange={handleChange} placeholder=" Write requirements.."/>
	             <button onClick={checkText} >Check</button>
	        </div>
            {popUp && <PopUp setPopUp={setPopUp}/>}
        </div>
    );
}

export default TextForm;