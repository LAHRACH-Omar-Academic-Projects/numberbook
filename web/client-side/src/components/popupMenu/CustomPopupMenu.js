import React from 'react';
import { Link, Redirect } from 'react-router-dom';
import { FaUser as UserIcon } from 'react-icons/fa';
import { BiRun as LogOutIcon } from 'react-icons/bi';
import adminService from "../../services/admin-service";
import './CustomPopupMenu.css';

const CustomPopupMenu = (props) => {

    const logOut = () => {
        adminService.logout();
        props.history.push("/login");
    };

    return (
        <div className="CustomPopupMenu">
            <div className="Header">
                <span>WELCOME!</span>
            </div>
            <ul>
                <li className="popup-item">
                    <Link to={"/profile"} className="Link">
                        <UserIcon/>
                        <span className="item-text">My profile</span>
                    </Link>
                </li>
                <li className="popup-item">
                    <div onClick={logOut}>
                        <LogOutIcon/>
                        <span className="item-text">Logout</span>
                    </div>
                </li>
            </ul>
        </div>
    );
}

export default CustomPopupMenu;