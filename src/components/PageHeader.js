import React from "react";
import Login from './Login'
import Register from './Register'
import Favorites from './Favorites'
import { Button, Col, Row, Layout } from 'antd'

const { Header } = Layout

function PageHeader({loggedIn, signoutOnClick, signinOnSuccess, favoriteItems}) {
    return (
        <Header>
            <Row justify="space-between">
                <Col>
                    {
                        loggedIn &&
                        <Favorites data={favoriteItems} />
                    }
                </Col>
                <Col>
                    <h1 className="header-title">Twitch Browser</h1>
                </Col>
                <Col>
                    {
                        loggedIn ?
                            <Button shape="round" onClick={signoutOnClick}>
                                Logout</Button> :
                            (
                                <>
                                    <Login onSuccess={signinOnSuccess} />
                                    <Register />
                                </>
                            )
                    }
                </Col>
            </Row>
        </Header>
    )
}

export default PageHeader