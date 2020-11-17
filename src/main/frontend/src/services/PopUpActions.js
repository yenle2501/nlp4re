 import axios from 'axios'

const URL = 'http://localhost:8080/api/user';	

	class Users{
		getUsers(){
			return axios.get(URL);
		}
	}
	
	export default new Users()