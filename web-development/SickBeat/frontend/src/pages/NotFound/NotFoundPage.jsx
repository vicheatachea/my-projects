import {useNavigate} from 'react-router-dom';
import {FaExclamationTriangle} from 'react-icons/fa';
import styles from './NotFoundPage.module.css';

const NotFoundPage = () => {
    const navigate = useNavigate();

    return (
        <section className={styles.container}>
            <FaExclamationTriangle className={styles.icon}/>
            <h1 className={styles.title}>404 Not Found</h1>
            <p className={styles.message}>This page does not exist</p>
            <button className={styles.backButton} onClick={() => navigate(-1)}>Go Back</button>
        </section>
    );
};

export default NotFoundPage;
