import React, { useState, useEffect } from 'react';

import { FiMenu as Toggler } from "react-icons/fi";
import { MdNotificationsNone as NotificationIcon } from "react-icons/md";
import { TiUser as User } from "react-icons/ti";
import { FaMapMarkedAlt as HomeIcon } from 'react-icons/fa';
import { FaUsersCog as Users } from 'react-icons/fa';
import { SiDiscover as Disc } from "react-icons/si";

import CustomPopupMenu from '../components/popupMenu/CustomPopupMenu';
import Map from './Map';
import Administration from './Administration';

const Notifications = (props) => {
    const [exists, setExists] = useState(false);

    useEffect(() => {
        if (props.notificationList.length !== 0) {
            setExists(true);
        }
        else {
            setExists(false);
        }
    })

    return (
        <div className="Notifications">
            <div className="Header">
                <h3>Notifications</h3>
            </div>
            {!exists && (
                <div className="NoNotifications">
                    <NotificationIcon size={45} />
                    <span>No Notificattions</span>
                </div>
            )}
            <div className="Footer">
                <span>Mark all as read</span>
            </div>
        </div>
    );
}

function Home(props) {
    const [marginTop, setMarginTop] = useState(5);
    const [itemSelected, setItemSelected] = useState(0);
    const [expanded, setExpanded] = useState(true);
    const [scrolling, setScrolling] = useState(false);
    const [showAside, setShowAside] = useState(true);
    const [showNotifications, setShowNotifications] = useState(false);
    const [showNavbarPopup, setShowNavbarPopup] = useState(false);
    const [showNotification, setShowNotification] = useState(false);
    const [notificationList, setNotificationList] = useState([]);

    useEffect(() => {
        window.addEventListener("scroll", () => {
            setScrolling(true);
            if (window.scrollY === 0) {
                setScrolling(false);
            }
        })
    }, []);

    const activateItem = (index) => {
        var margin = index * 5 + 5 + index * 70;
        setMarginTop(margin);
        setExpanded(true);
        setItemSelected(null);
        setTimeout(() => {
            setItemSelected(index);
        }, 200);
    }

    return (
        <div className="Container">
            <header style={{ boxShadow: scrolling ? "0px 0px 10px #ccc" : "" }}>
                <nav>
                    <ul className="Navbar-left">
                        <li onClick={() => { setExpanded(!expanded); setShowAside(true) }}>
                            <Toggler size={22} color={"rgba(40, 53, 147, 0.64)"} />
                        </li>
                        <li onClick={() => activateItem(0)}>
                        
                            <span>Comment allez-vous</span>
                        </li>
                    </ul>
                    <ul className="Navbar-right">
                        <li className="Notification-item" onClick={() => setShowNotifications(!showNotifications)}>
                            {notificationList.length !== 0 && (
                                <Disc className="Disc" size={12} />
                            )}
                            <NotificationIcon size={25} color="rgba(0, 0, 0, 0.65)" />
                        </li>
                        {showNotifications && (
                            <Notifications showNotification={showNotification} notificationList={notificationList} />
                        )}
                        <div>
                            <li className="User-item" onClick={() => setShowNavbarPopup(!showNavbarPopup)}>
                                <User size={35} color="rgba(255,255,255,0.8)" />
                            </li>
                            {showNavbarPopup && (
                                <CustomPopupMenu history={props.history} />
                            )}
                        </div>
                    </ul>
                </nav>
            </header>

            <aside style={{ display: showAside ? 'flex' : "none" }}>
                <nav>
                    <ul>
                        <li style={{ marginTop: marginTop + "px" }}></li>
                    </ul>
                    <ul>
                        <li onClick={() => activateItem(0)}>
                            <HomeIcon size={30} color={itemSelected === 0 ? "rgb(50, 66, 185)" : "rgba(0, 0, 0, 0.25)"} />
                            <span style={{ color: itemSelected === 0 ? "rgb(50, 66, 185)" : "rgba(0, 0, 0, 0.54)" }}>Home</span>
                        </li>
                        <li onClick={() => activateItem(1)}>
                            <Users size={28} color={itemSelected === 1 ? "rgb(50, 66, 185)" : "rgba(0, 0, 0, 0.25)"} />
                            <span style={{ color: itemSelected === 1 ? "rgb(50, 66, 185)" : "rgba(0, 0, 0, 0.54)" }}>Users</span>
                        </li>
                    </ul>
                </nav>
            </aside>

            <section style={{ marginLeft: expanded ? "298px" : "120px" }}>
                {itemSelected === 0 && (
                    <Map activateItem={activateItem}/>
                )}
                {itemSelected === 1 && (
                    <Administration setShowNotification={setShowNotification} />
                )}
            </section>

            <section onClick={() => { setExpanded(false); setShowAside(false) }} style={{ display: showAside ? 'flex' : "none" }}>

            </section>
        </div>
    );
}

export default Home;