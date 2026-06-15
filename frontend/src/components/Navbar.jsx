import { Link, useNavigate } from 'react-router-dom'
import { logout } from '../api'
import styles from './Navbar.module.css'

export default function Navbar({ admin = false }) {
  const navigate = useNavigate()

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <header className={styles.header}>
      <nav className={styles.nav}>
        <Link to={admin ? '/dashboard' : '/'} className={styles.brand}>
          Weather App
        </Link>

        <div className={styles.actions}>
          {admin ? (
            <>
              <Link
                to="/dashboard"
                className={styles.link}
              >
                Dashboard
              </Link>
              <Link
                to="/users"
                className={styles.link}
              >
                Users
              </Link>
              <button onClick={handleLogout} className={styles.logoutBtn}>
                Sign Out
              </button>
            </>
          ) : (
            <Link to="/login" className={styles.adminBtn}>
              Admin
            </Link>
          )}
        </div>
      </nav>
    </header>
  )
}
