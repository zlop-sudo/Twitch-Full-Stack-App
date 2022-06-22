import { Button, Form, Input, message, Modal } from 'antd'
import React, { useState } from 'react'
import { LockOutlined, UserOutlined } from '@ant-design/icons'
import { login } from '../utils'

function Login({ onSuccess }) {
  const [displayModal, setDisplayModal] = useState(false)

  const handleCancel = () => {
    setDisplayModal(false)
  }

  const signinOnClick = () => {
    setDisplayModal(true)
  }

  const onFinish = (data) => {
    login(data)
      .then((data) => {
        setDisplayModal(false)
        message.success(`Welcome back, ${data.name}`)
        onSuccess()
      }).catch((err) => {
        message.error(err.message)
      })
  }

  return (
    <>
      <Button shape="round" onClick={signinOnClick} style={{ marginRight: '20px' }}>
        Login</Button>
      <Modal
        title="Log in"
        visible={displayModal}
        onCancel={handleCancel}
        footer={null}
        destroyOnClose={true}
      >
        <Form
          name="normal_login"
          onFinish={onFinish}
          preserve={false}
        >
          <Form.Item
            name="user_id"
            rules={[{ required: true, message: 'Please input your Username!' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Username" />
          </Form.Item>
          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Please input your Password!' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Password"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              Login</Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default Login