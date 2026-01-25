import { BrowserRouter, Routes, Route } from "react-router";
import Layout from "./Layout";
import App from "../pages/App";
import EndpointsTesting from "../pages/EndpointsTesting";

export default function Router(){
    return(
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Layout/>}>
                    <Route index element={<App/>}/>
                    <Route path="/testing" element={<EndpointsTesting/>}/>
                </Route>
            </Routes>
        </BrowserRouter>
    )
}