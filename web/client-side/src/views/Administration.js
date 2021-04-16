import React, { useEffect } from 'react';
import { FaUserCircle as Avatar } from "react-icons/fa";
import { FaListAlt as NoClients } from "react-icons/fa";
import { CgDetailsMore as NoDetails } from "react-icons/cg";
import { BiMehBlank as NoUsers } from "react-icons/bi";
import { useState } from 'react';
import userService from '../services/user-service';







const UserItem = (props) => {
    return (
        <li onClick={props.handleClick} style={{ backgroundColor: props.selectedItem === props.position ? "rgba(255, 255, 255, 0.8)" : "" }}>
            <Avatar color="rgba(40, 130, 150, 0.5)" size={45} />
            <div className="Info">
                <span className="Name">{props.username}</span>
                <span className="Agency">Nombre d'amis</span>
            </div>
        </li>
    );
}






const FriendItem = (props) => {
    return (
        <li>
            <div className="Info">
                <div>
                    <span className="FullName">Nom et Prenom :  {props.username}</span>
                    <span className="CIN">Email : {props.email}</span>
                </div>
                <div>
                    <span className="NumberOfRecharges">Sexe: {props.gender}</span>
                    <span className="NumberOfMoneyTransfers">Téléphone : {props.phoneNumber}</span>
                </div>
            </div>
        </li>
    );
}









const UserDetails = (props) => {
    const [friends, setFriends] = useState([]);

    useEffect(() => {
        const getFriends = async () => {
            let friends = [];
            props.user.responses.forEach((res) => {
                if (res.status === 2) {
                    friends.push(res.responder);
                }
            })
            props.user.requests.forEach((res) => {
                if (res.status === 2) {
                    friends.push(res.requester);
                }
            })
            setFriends(await friends);
            console.log(friends);
        }
        getFriends();
    }, [props.user])

    return (
        <div className="Details">
            <div className="Header">
                <div className="User">
                    <Avatar color="rgba(40, 130, 150, 0.5)" size={60} />
                    <div className="Info">
                            <span className="FullName">Nom et Prenom :  {props.user.lastName + " " + props.user.firstName}</span>
                            <span className="CIN">Email : {props.user.email}</span>
                            <span className="NumberOfRecharges">Sexe: {props.user.gender}</span>
                            <span className="NumberOfMoneyTransfers">Téléphone : {props.user.phoneNumber}</span>
                    </div>
                </div>
            </div>
            <div className="Body">
                <div className="ClientList">
                    <ul>
                        {friends.map((friend) => {
                            return (
                                <FriendItem
                                    key={friend.id}
                                    username={friend.lastName + " " + friend.firstName}
                                    gender={friend.gender}
                                    email={friend.email}
                                    phoneNumber={friend.phoneNumber}
                                />
                            )
                        })}
                        {friends.length === 0 && (
                            <div className="NoClients">
                                <NoClients size={45} />
                                <span>The list of clients is empty, try to add one by clicking on "+" below</span>
                            </div>
                        )}
                    </ul>
                </div>
            </div>
        </div >
    );
}











function Administration() {
    const [users, setUsers] = useState([]);
    const [selected, setSelected] = useState(0);

    if (selected !== 0 && selected >= users.length) {
        setSelected(users.length - 1);
    }

    useEffect(() => {
        userService.getAllUsers()
            .then((res) => {
                res.data.sort((a, b) => (a.id > b.id) ? -1 : ((b.id > a.id) ? 1 : 0));
                setUsers(res.data);
            })
    }, [])

    return (
        <div className="Administration-Card">
            <div className="Card-left">
                <div className="Header">
                    <div className="Sum">
                        <span>{users.length} Utilisateurs</span>
                    </div>
                </div>
                <div className="Body">
                    <ul>
                        {users.length !== 0 ? (users.map((user, pos) => {
                            return (
                                <UserItem
                                    key={user.id}
                                    position={pos}
                                    online={user.online}
                                    username={user.lastName + " " + user.firstName}
                                    email={user.email}
                                    phoneNumber={user.phoneNumber}
                                    gender={user.gender}
                                    birthday={user.birthday}
                                    handleClick={() => setSelected(pos)}
                                    selectedItem={selected === users.length ? selected - 1 : selected}
                                />
                            );
                        }))
                            : (
                                <div className="NoUsers">
                                    <NoUsers size={45} />
                                    <span>The list of users is empty, to add a new User click on "+" symbol on the right-bottom</span>
                                </div>
                            )
                        }
                    </ul>
                </div>
            </div>
            <div className="Card-right">
                {users.length !== 0 ? (
                    <UserDetails
                        user={selected === users.length ? users[selected - 1] : users[selected]}
                    />
                ) :
                    <div className="NoDetails">
                        <NoDetails size={45} />
                        <span>No user selected, select a user to see more details, or add new user if there isn't</span>
                    </div>
                }
            </div>
        </div>
    );
}

export default Administration;