import React from 'react'
import ReactDOM from 'react-dom'
import Files from './Files'
import Search from './Search'
import Torrents from './Torrents'

class App extends React.Component {

    static getCurrentViewType() {
        const hash = window.location.hash
        if (hash === '#/search') {
            return 'search'
        } else if (hash === '#/torrents') {
            return 'torrents'
        } else if (hash === '#/files') {
            return 'files'
        }
        return 'error'
    }

    static changeViewType(viewType) {
        window.location.hash = `#/${viewType}`
    }

    constructor(props) {
        super(props)
        this.state = {
            viewType: App.getCurrentViewType(),
        }
    }

    componentDidMount() {
        window.addEventListener('hashchange', this.handleHashChange.bind(this))
    }

    componentWillUnmount() {
        window.removeEventListener('hashchange', this.handleHashChange)
    }

    handleHashChange() {
        this.setState({ viewType: App.getCurrentViewType() })
    }

    render() {
        const viewType = this.state.viewType
        return (
            <section className="section">
                <div className="container">
                    <div className="tabs is-centered">
                        <ul>
                            <li className={(viewType === 'search') ? 'is-active' : ''}>
                                <a onClick={() => App.changeViewType('search')}>Search</a>
                            </li>
                            <li className={(viewType === 'torrents') ? 'is-active' : ''}>
                                <a onClick={() => App.changeViewType('torrents')}>Torrents</a>
                            </li>
                            <li className={(viewType === 'files') ? 'is-active' : ''}>
                                <a onClick={() => App.changeViewType('files')}>Files</a>
                            </li>
                        </ul>
                    </div>

                    {viewType === 'search' && (
                        <Search/>
                    )}

                    {viewType === 'torrents' && (
                        <Torrents/>
                    )}

                    {viewType === 'files' && (
                        <Files/>
                    )}

                    {viewType === 'error' && (
                        <div className="notification is-danger">
                            Error
                        </div>
                    )}
                </div>
            </section>
        )
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('root')
)