import React from 'react';
import { Link } from 'react-router-dom';
import styles from './Footer.module.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFacebook, faInstagram, faXTwitter } from '@fortawesome/free-brands-svg-icons';

function Footer({ theme }) {
    return (
        <footer className={`${styles.footer} ${theme}`}>
            <div className={styles.rights}>&copy; 2024 SickBeat<br /> All rights reserved</div>
            <nav>
                <ul className={styles.nav}>
                    <li className={styles.section}>
                        <Link to="#"><b>Company</b></Link>
                        <div className={styles.items}>
                            <Link to="#" className={styles.text}>About Us</Link>
                            <Link to="#" className={styles.text}>Career Opportunities</Link>
                            <Link to="#" className={styles.text}>Objectives</Link>
                        </div>
                    </li>
                    <li className={styles.section}>
                        <Link to="#"><b>Help</b></Link>
                        <div className={styles.items}>
                            <Link to="#" className={styles.text}>Customer Support</Link>
                            <Link to="#" className={styles.text}>Terms & Conditions</Link>
                            <Link to="#" className={styles.text}>Privacy Policy</Link>
                        </div>
                    </li>
                    <div className={styles.social}>
                        <li><a href="https://www.facebook.com"><FontAwesomeIcon icon={faFacebook} size="lg" /></a></li>
                        <li><a href="https://www.Instagram.com"><FontAwesomeIcon icon={faInstagram} size='lg' /></a></li>
                        <li><a href="https://x.com"><FontAwesomeIcon icon={faXTwitter} size='lg' /></a></li>
                    </div>
                </ul>
            </nav>
        </footer>
    );
}

export default Footer;
