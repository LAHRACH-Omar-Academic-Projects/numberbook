import React, { useState, useEffect } from 'react';

import { TiUser as UsernameIcon } from "react-icons/ti";
import { RiLockPasswordFill as PasswordIcon } from "react-icons/ri";

import './CustomInput.css';

const Icon = (props) => {
    const color = props.color;
    const style = props.style;
    const size = 20;

    switch (style) {
        case "username":
            return (
                <UsernameIcon size={size} color={color} />
            )
        case "password":
            return (
                <PasswordIcon size={size} color={color} />
            )
        default:
            break;
    }
}

const CustomInput = (props) => {
    const [showWarning, setShowWarning] = useState(true);
    const [color, setColor] = useState("")

    const style = props.inputStyle;
    const type = props.type;
    const placeholder = props.placeholder;
    const onChange = props.onChange;
    const validity = props.validity;

    useEffect(() => {
        if (validity !== null) {
            if (validity.isValid) {
                setColor("rgb(0, 199, 83)");
                setShowWarning(false);
            }
            else if (!validity.isValid) {
                setColor("orange");
                setShowWarning(true);
            }
        }
        else {
            setColor("rgb(98, 111, 133)");
            setShowWarning(true);
        }
    }, [color, validity]);

    return (
        <div className="CustomInput">
            <div className="CustomInput-field">
                <div className="CustomInput-icon">
                    <Icon color={color} style={style} />
                </div>
                <input
                    type={type}
                    className={"CustomInput-default"}
                    placeholder={placeholder}
                    onChange={(text) => onChange(text)}
                />
            </div>
            {validity !== null && showWarning && (
                <div className="CustomInput-warning">
                    {validity.message}
                </div>
            )}
        </div>
    )
}

export default CustomInput;