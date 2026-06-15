import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api'
import styles from './Login.module.css'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)

    const data = await login(email, password)
    setLoading(false)

    if (data.success) {
      navigate('/dashboard')
    } else {
      setError('Invalid email or password.')
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        <div className={styles.header}>
          <Link to="/" className={styles.brand}>Weather App</Link>
          <h1 className={styles.title}>Admin Sign In</h1>
          <p className={styles.subtitle}>Sign in to manage users and view reports.</p>
        </div>

        <div className={styles.card}>
          {error && (
            <div className={styles.errorBox}>{error}</div>
          )}

          <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.field}>
              <label className={styles.label} htmlFor="email">Email address</label>
              <input
                id="email"
                type="email"
                className={styles.input}
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="admin@example.com"
                required
                autoComplete="email"
              />
            </div>

            <div className={styles.field}>
              <label className={styles.label} htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                className={styles.input}
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
                autoComplete="current-password"
              />
            </div>

            <button type="submit" className={styles.button} disabled={loading}>
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
