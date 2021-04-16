import React, { useState } from 'react';
import Loader from 'react-loaders';

import 'loaders.css';
import './CustomButton.css';

const CustomButton = (props) => {
    const text = props.text;
    const loading = props.loading;
    const [clicked, setClicked] = useState(false);

    const handleClick = () => {
        setClicked(true)
        setTimeout(() => {
            setClicked(false);
        }, 200);
    }

    return(
        <button 
            className={!clicked ? "CustomButton" : "CustomButton-clicked"}
            disabled={loading} 
            onClick={() => handleClick()}>
            {loading 
                ? <Loader type="line-scale-pulse-out" className="Loader" active/>
                : text
            } 
        </button>
    )
}

export default CustomButton;