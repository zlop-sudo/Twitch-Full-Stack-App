import React, { useState } from 'react'
import { Menu, Button, Drawer } from 'antd'
import { EyeOutlined, YoutubeOutlined, VideoCameraOutlined, StarFilled } from '@ant-design/icons'
import FavoriteMenu from './FavoriteMenu'
 
const { SubMenu } = Menu
const MenuKey = {
  Streams: 'streams',
  Videos: 'videos',
  Clips: 'clips'
}

function Favorites({data}) {
  
  const [displayDrawer, setDisplayDrawer] = useState(false)
  const { VIDEO, STREAM, CLIP } = data
 
  const onDrawerClose = () => {
    setDisplayDrawer(false)
  }
 
  const onFavoriteClick = () => {
    setDisplayDrawer(true)
  }

  return (
    <>
      <Button type="primary" shape="round" onClick={onFavoriteClick} icon={<StarFilled />}>
        My Favorites</Button>
      <Drawer
        title="My Favorites"
        placement="right"
        width={720}
        visible={displayDrawer}
        onClose={onDrawerClose}
      >
        <Menu
          mode="inline"
          defaultOpenKeys={[MenuKey.Streams]}
          style={{ height: '100%', borderRight: 0 }}
          selectable={false}
        >
          <SubMenu key={MenuKey.Streams} icon={<EyeOutlined />} title="Streams">
            <FavoriteMenu items = {STREAM}/>
          </SubMenu>
          <SubMenu key={MenuKey.Videos} icon={<YoutubeOutlined />} title="Videos">
            <FavoriteMenu items = {VIDEO}/>
          </SubMenu>
          <SubMenu key={MenuKey.Clips} icon={<VideoCameraOutlined />} title="Clips">
            <FavoriteMenu items = {CLIP}/>
          </SubMenu>
        </Menu>
      </Drawer>
    </>
  )
}
 
export default Favorites