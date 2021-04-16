import { Map as MapContainer, InfoWindow, Marker, GoogleApiWrapper } from 'google-maps-react';
import { Component } from 'react';
import positionsService from '../services/positions-service';
import userService from '../services/user-service';

export class Map extends Component {
    state = {
        markers: []
    }

    componentDidMount() {
        userService.getAllUsers()
            .then((res) => {
                let users = res.data;
                let markers = [];
                users.forEach(user => {
                    positionsService.getLastPosition(user)
                        .then((res) => {
                            markers.push(res.data);
                        })
                });
                setTimeout(() => {
                    this.setState({ markers: markers });
                }, 100);
            })
    }

    render() {
        return (
            <MapContainer c
                styles={this.props.mapStyles}
                google={this.props.google} zoom={8}
                initialCenter={{
                    lat: 33,
                    lng: -8
                  }}>

                {this.state.markers.map((marker) => {
                    return (
                        <Marker
                            title={marker !== "" && marker.user.lastName + " " + marker.user.firstName}
                            key={marker.id}
                            position={{
                                lat: marker.latitude,
                                lng: marker.longitude
                            }}
                        >
                            <h1>{marker.id}</h1>
                        </Marker>
                    )
                })}


                <InfoWindow
                    onOpen={this.windowHasOpened}
                    onClose={this.windowHasClosed}
                >
                    <div>

                    </div>
                </InfoWindow>
            </MapContainer>

        );
    }
}


Map.defaultProps = {
    mapStyles: [
        {
            elementType: "geometry",
            stylers: [
                {
                    "color": "#242f3e"
                }
            ]
        },
        {
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#706781"
                }
            ]
        },
        {
            elementTyp: "labels.text.stroke",
            stylers: [
                {
                    "color": "#242f3e"
                }
            ]
        },
        {
            featureType: "administrative.locality",
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#c28ef3"
                }
            ]
        },
        {
            featureType: "poi",
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#f06376"
                }
            ]
        },
        {
            featureType: "poi.park",
            elementType: "geometry",
            stylers: [
                {
                    "color": "#263c3f"
                }
            ]
        },
        {
            featureType: "poi.park",
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#6b9a76"
                }
            ]
        },
        {
            featureType: "road",
            elementType: "geometry",
            stylers: [
                {
                    "color": "#38414e"
                }
            ]
        },
        {
            featureType: "road",
            elementType: "geometry.stroke",
            stylers: [
                {
                    "color": "#212a37"
                }
            ]
        },
        {
            featureType: "road",
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#9ca5b3"
                }
            ]
        },
        {
            featureType: "road.highway",
            elementType: "geometry",
            stylers: [
                {
                    "color": "#3856b8b2"
                }
            ]
        },
        {
            featureType: "road.highway",
            elementType: "geometry.stroke",
            stylers: [
                {
                    "color": "#1f2835"
                }
            ]
        },
        {
            featureType: "road.highway",
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#82a0df"
                }
            ]
        },
        {
            featureType: "transit",
            elementType: "geometry",
            stylers: [
                {
                    "color": "#2f3948"
                }
            ]
        },
        {
            featureType: "transit.station",
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#72c2f0"
                }
            ]
        },
        {
            featureType: "water",
            elementType: "geometry",
            stylers: [
                {
                    "color": "#17263c"
                }
            ]
        },
        {
            featureType: "water",
            elementType: "labels.text.fill",
            stylers: [
                {
                    "color": "#515c6d"
                }
            ]
        },
        {
            featureType: "water",
            elementType: "labels.text.stroke",
            stylers: [
                {
                    "color": "#17263c"
                }
            ]
        }
    ]
}
export default GoogleApiWrapper({
    apiKey: ("AIzaSyDbILEX8s4Zwhchh9jwm569U91x3wyMGSs")
})(Map)