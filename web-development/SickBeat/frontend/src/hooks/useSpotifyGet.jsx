import {useState, useEffect} from "react";
import axios from "axios";

export const useSpotifyGet = (url) => {
    const [data, setData] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(url);
                setData(response.data);
            } catch (error) {
                console.error(error.response.status, error.response.data);
                setError(error.response.status);
            }
        };

        fetchData();
    }, [url]);

    return {data, error};
}
