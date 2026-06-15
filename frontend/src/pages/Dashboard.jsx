import { useState } from 'react'
import Navbar from '../components/Navbar'
import { saveUser, getWeather } from '../api'
import styles from './Dashboard.module.css'

function AddUserCard() {
  const [form, setForm] = useState({ name: '', city: '', address: '' })
  const [status, setStatus] = useState(null) // 'success' | 'error' | null
  const [loading, setLoading] = useState(false)

  function handleChange(e) {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setStatus(null)
    setLoading(true)

    const data = await saveUser(form.name.trim(), form.city.trim(), form.address.trim())
    setLoading(false)

    if (data.success) {
      setStatus('success')
      setForm({ name: '', city: '', address: '' })
      setTimeout(() => setStatus(null), 3000)
    } else {
      setStatus('error')
    }
  }

  return (
    <div className={styles.card}>
      <div className={styles.cardHeader}>
        <h2 className={styles.cardTitle}>Add User</h2>
        <p className={styles.cardDesc}>Save a new user to the database.</p>
      </div>

      <form onSubmit={handleSubmit} className={styles.form}>
        <div className={styles.field}>
          <label className={styles.label} htmlFor="name">Full Name</label>
          <input
            id="name"
            name="name"
            type="text"
            className={styles.input}
            value={form.name}
            onChange={handleChange}
            placeholder="Jane Smith"
            maxLength={100}
            required
          />
        </div>

        <div className={styles.field}>
          <label className={styles.label} htmlFor="city">City</label>
          <input
            id="city"
            name="city"
            type="text"
            className={styles.input}
            value={form.city}
            onChange={handleChange}
            placeholder="Kathmandu"
            maxLength={100}
            required
          />
        </div>

        <div className={styles.field}>
          <label className={styles.label} htmlFor="address">Address</label>
          <input
            id="address"
            name="address"
            type="text"
            className={styles.input}
            value={form.address}
            onChange={handleChange}
            placeholder="123 Main St"
            maxLength={200}
            required
          />
        </div>

        <button type="submit" className={styles.button} disabled={loading}>
          {loading ? 'Saving...' : 'Save User'}
        </button>
      </form>

      {status === 'success' && (
        <div className={styles.successMsg}>User saved successfully.</div>
      )}
      {status === 'error' && (
        <div className={styles.errorMsg}>Failed to save user. Please try again.</div>
      )}
    </div>
  )
}

function WeatherCard() {
  const [city, setCity] = useState('')
  const [weather, setWeather] = useState(null)
  const [error, setError] = useState(false)
  const [loading, setLoading] = useState(false)

  async function handleSearch(e) {
    e.preventDefault()
    const trimmed = city.trim()
    if (!trimmed) return

    setLoading(true)
    setWeather(null)
    setError(false)

    const data = await getWeather(trimmed)
    setLoading(false)

    if (data.error) {
      setError(true)
    } else {
      setWeather(data)
    }
  }

  return (
    <div className={styles.card}>
      <div className={styles.cardHeader}>
        <h2 className={styles.cardTitle}>Check Weather</h2>
        <p className={styles.cardDesc}>Get current conditions for any city.</p>
      </div>

      <form onSubmit={handleSearch} className={styles.form}>
        <div className={styles.field}>
          <label className={styles.label} htmlFor="weatherCity">City</label>
          <input
            id="weatherCity"
            type="text"
            className={styles.input}
            value={city}
            onChange={e => setCity(e.target.value)}
            placeholder="New York"
            maxLength={100}
          />
        </div>
        <button type="submit" className={styles.button} disabled={loading}>
          {loading ? 'Loading...' : 'Get Weather'}
        </button>
      </form>

      {error && (
        <div className={styles.errorMsg}>City not found. Check the spelling and try again.</div>
      )}

      {weather && (
        <div className={styles.weatherResult}>
          <div className={styles.weatherRow}>
            <span className={styles.weatherLabel}>Temperature</span>
            <span className={styles.weatherValue}>{weather.temperature}&deg;C</span>
          </div>
          <div className={styles.weatherRow}>
            <span className={styles.weatherLabel}>Feels Like</span>
            <span className={styles.weatherValue}>{weather.feelsLike}&deg;C</span>
          </div>
          <div className={styles.weatherRow}>
            <span className={styles.weatherLabel}>Humidity</span>
            <span className={styles.weatherValue}>{weather.humidity}%</span>
          </div>
          <div className={styles.weatherRow}>
            <span className={styles.weatherLabel}>Conditions</span>
            <span className={styles.weatherValue} style={{ textTransform: 'capitalize' }}>
              {weather.description}
            </span>
          </div>
        </div>
      )}
    </div>
  )
}

export default function Dashboard() {
  return (
    <div className={styles.page}>
      <Navbar admin />

      <main className={styles.main}>
        <div className={styles.pageHeader}>
          <h1 className={styles.pageTitle}>Dashboard</h1>
          <p className={styles.pageDesc}>Manage users and monitor weather conditions.</p>
        </div>

        <div className={styles.grid}>
          <AddUserCard />
          <WeatherCard />
        </div>
      </main>
    </div>
  )
}
