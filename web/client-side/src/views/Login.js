import React, { useState } from 'react';

import CustomButton from '../components/customButton/CustomButton';
import CustomInput from '../components/customInput/CustomInput';

import adminService from '../services/admin-service';

import { MdError as Danger } from "react-icons/md";


function Login(props) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [usernameValidity, setUsernameValidity] = useState(null);
    const [passwordValidity, setPasswordValidity] = useState(null);

    const [loading, setLoading] = useState(false);

    const [message, setMessage] = useState("");

    const onChangeUsername = (e) => {
        const username = e.target.value;
        if (username === "") {
            setUsernameValidity({ "isValid": false, "message": "This field is required!" })
        }
        else if (username.length < 3 || username.length > 20) {
            setUsernameValidity({ "isValid": false, "message": "The username must be between 3 and 20 characters." })
        }
        else {
            setUsernameValidity({ "isValid": true, "message": "" })
            setUsername(username);
        }
    };

    const onChangePassword = (e) => {
        const password = e.target.value;
        if (password === "") {
            setPasswordValidity({ "isValid": false, "message": "This field is required!" })
        }
        else if (password.length < 8 || password.length > 40) {
            setPasswordValidity({ "isValid": false, "message": "The password must be between 8 and 40 characters." })
        }
        else {
            setPasswordValidity({ "isValid": true, "message": "" })
            setPassword(password);
        }
    }

    const handleLogin = (e) => {
        e.preventDefault();
        setLoading(true);
        adminService.login(username, password)
            .then((res) => {
            
                    props.history.push("/home");
                    setMessage("l'utilisateur avec ses identifiés n'est pas autorisé");
                    setLoading(false);
                
            })
            .catch(() => {
                setLoading(false);
            })
    }

    return (
        <form className="Login" onSubmit={(e) => handleLogin(e)}>
            <div className="Login-header">
                <span className="Login-title">Sign In</span>
            </div>
            <div className="Login-body">
                <CustomInput inputStyle={"username"} type={"text"} placeholder={"Username"} onChange={onChangeUsername} validity={usernameValidity} />
                <CustomInput inputStyle={"password"} type={"password"} placeholder={"Password"} onChange={onChangePassword} validity={passwordValidity} />
                <CustomButton text={"Sign in"} loading={loading} />
            </div>
            <div className="Login-footer">
                {message && !loading && (
                    <div className="Login-danger">
                        <Danger size={45} />
                        {message}
                    </div>
                )}
            </div>
        </form>
    );
}

export default Login;