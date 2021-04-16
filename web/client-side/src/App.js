import { Route, Router, Switch } from 'react-router-dom';
import { history } from './helpers/history';

import Home from './views/Home';
import Login from './views/Login';

import './App.css';

function App() {
  return (
    <div className="App">
      <Router history={history}>
            <Switch>
              <Route exact path={["/", "/home"]} component={Home}/>
              <Route exact path="/login" component={Login} />
            </Switch>
        </Router>
    </div>
  );
}

export default App;
