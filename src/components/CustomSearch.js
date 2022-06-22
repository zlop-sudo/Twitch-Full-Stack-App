import React, { useState } from 'react'
import { Button, Form, Input, message, Modal } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import { searchGameByName } from '../utils'
 
function CustomSearch({onSuccess}) {

  const [displayModal, setDisplayModal] = useState(false)
 
  const handleCancel = () => {
    setDisplayModal(false)
  }
 
  const searchOnClick = () => {
    setDisplayModal(true)
  }
 
  const onSubmit = (data) => {
    searchGameByName(data.game_name)
      .then((data) => {
        setDisplayModal(false)
        onSuccess(data)
      })
      .catch((err) => {
        message.error(err.message)
      })
  }

  return (
    <>
      <Button shape="round" onClick={searchOnClick} icon={<SearchOutlined />} style={{ marginLeft: '20px', marginTop: '20px'}}>
        Custom Search </Button>
      <Modal
        title="Search"
        visible={displayModal}
        onCancel={handleCancel}
        footer={null}
      >
        <Form
          name="custom_search"
          onFinish={onSubmit}
        >
          <Form.Item
            name="game_name"
            rules={[{ required: true, message: 'Please enter a game name' }]}
          >
            <Input placeholder="Game name" />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              Search</Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
 
export default CustomSearch